package pl.beone.promena.alfresco.module.connector.activemq.external.transformation

import mu.KotlinLogging
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.repository.NodeRef
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import pl.beone.promena.alfresco.module.connector.activemq.configuration.service.ContextProvider
import pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq.TransformerSender
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaModel.PROPERTY_EXECUTION_IDS
import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.node.toNodeRefs
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.Retry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.node.DataDescriptorGetter
import pl.beone.promena.alfresco.module.core.contract.node.NodeInCurrentTransactionVerifier
import pl.beone.promena.alfresco.module.core.contract.node.NodesChecksumGenerator
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager.PromenaMutableTransformationManager
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutorValidator
import pl.beone.promena.alfresco.module.core.extension.getPropertyWithEmptySupport
import pl.beone.promena.alfresco.module.core.extension.getRequiredPropertyWithResolvedPlaceholders
import pl.beone.promena.alfresco.module.core.extension.start
import pl.beone.promena.core.applicationmodel.transformation.transformationDescriptor
import pl.beone.promena.transformer.contract.communication.CommunicationParameters
import pl.beone.promena.transformer.contract.data.DataDescriptor
import pl.beone.promena.transformer.contract.transformation.Transformation
import java.io.Serializable
import java.util.*

/**
 * Provides ActiveMQ bridge between Alfresco Content Services and Promena.
 * This implementation uses `promena-connector-activemq` connector module so it's required to include it on Promena.
 *
 * The flow:
 * 1. Validates `postTransformationExecutor`
 * 2. Checks if the nodes from `nodeDescriptor` have been modified in the current transaction
 * 3. Generates a checksum of `nodeDescriptor`
 * 4. Converts `nodeDescriptor` to [DataDescriptor]
 * 5. Starts a new transformation execution
 * 6. Adds [PROPERTY_EXECUTION_IDS] to nodes from [NodeDescriptor]
 * 7. Performs a transaction asynchronously
 * 8. Returns [TransformationExecution]
 *
 * The flow of an asynchronous transaction execution:
 * 1. Performs a transformation on Promena ([HttpPromenaTransformer.execute])
 * 2. Verifies if the nodes from `nodeDescriptor` still exist
 * 3. Checks if the checksum of the nodes from `nodeDescriptor` haven't changed
 * 4. Saves the results of the transformation execution
 * 5. Injects dependencies into `postTransformationExecutor` and run it if set
 * 6. Cleans data
 * 7. Completes the transformation execution with the result
 * 8. In case of an error, another execution is run until the number of [Retry.maxAttempts] is reached.
 *    If the number is reached, it completes the transformation execution with an exception
 */
class ActiveMQPromenaTransformationExecutor(
    private val externalCommunicationParameters: CommunicationParameters,
    private val promenaMutableTransformationManager: PromenaMutableTransformationManager,
    private val retry: Retry,
    private val postTransformationExecutorValidator: PostTransformationExecutorValidator,
    private val nodeInCurrentTransactionVerifier: NodeInCurrentTransactionVerifier,
    private val nodesChecksumGenerator: NodesChecksumGenerator,
    private val dataDescriptorGetter: DataDescriptorGetter,
    private val transformerSender: TransformerSender,
    private val authorizationService: AuthorizationService,
    private val serviceRegistry: ServiceRegistry
) : PromenaTransformationExecutor {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun execute(
        transformation: Transformation,
        nodeDescriptor: NodeDescriptor,
        postTransformationExecutor: PostTransformationExecutor?,
        retry: Retry?,
        priority: Int
    ): TransformationExecution {
        val nodeRefs = nodeDescriptor.toNodeRefs()

        postTransformationExecutor?.let(postTransformationExecutorValidator::validate)

        nodeRefs.forEach(nodeInCurrentTransactionVerifier::verify)

        val nodesChecksum = nodesChecksumGenerator.generate(nodeRefs)
        val userName = authorizationService.getCurrentUser()
        val dataDescriptor = dataDescriptorGetter.get(nodeDescriptor)
        val transformationExecution = promenaMutableTransformationManager.startTransformation()

        val saveExecutionId = ContextProvider.getContext()!!.beanFactory.resolveEmbeddedValue("\${promena.connector.activemq.spring.jms.save-execution-id}").toBoolean()

        if (saveExecutionId) {
            addExecutionIdInNewTransaction(nodeRefs, transformationExecution.id)
        }

        logger.start(transformation, nodeDescriptor)

        val transformationParameters = TransformationParameters(
            transformation,
            nodeDescriptor,
            postTransformationExecutor,
            determineRetry(retry),
            dataDescriptor,
            nodesChecksum,
            0,
            userName
        )

        execute(
            transformationExecution.id,
            transformation,
            dataDescriptor,
            transformationParameters,
            priority
        )

        return transformationExecution
    }

    override fun execute(
        transformation: Transformation,
        nodeDescriptor: NodeDescriptor,
        postTransformationExecutor: PostTransformationExecutor?,
        retry: Retry?
    ): TransformationExecution {
        return execute(transformation, nodeDescriptor, postTransformationExecutor, retry, 4);
    }

    @Suppress("UNCHECKED_CAST")
    private fun addExecutionIdInNewTransaction(nodeRefs: List<NodeRef>, executionId: String) {
        serviceRegistry.retryingTransactionHelper.doInTransaction({
            nodeRefs.forEach { nodeRef ->
                val currentExecutionIds = ((serviceRegistry.nodeService.getProperty(nodeRef, PROPERTY_EXECUTION_IDS) as List<String>?) ?: emptyList())
                val updatedExecutionIds = currentExecutionIds + executionId

                serviceRegistry.nodeService.setProperty(nodeRef, PROPERTY_EXECUTION_IDS, updatedExecutionIds.toMutableList() as Serializable)
            }
        }, false, true)
    }

    internal fun execute(
        executionId: String,
        transformation: Transformation,
        dataDescriptor: DataDescriptor,
        transformationParameters: TransformationParameters,
        priority: Int = 4
    ) {
        transformerSender.send(
            executionId,
            transformationDescriptor(transformation, dataDescriptor, externalCommunicationParameters),
            transformationParameters,
            priority
        )
    }

    private fun determineRetry(retry: Retry?): Retry =
        retry ?: this.retry
}