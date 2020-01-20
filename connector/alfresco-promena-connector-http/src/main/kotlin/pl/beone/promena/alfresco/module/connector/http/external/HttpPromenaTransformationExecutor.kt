package pl.beone.promena.alfresco.module.connector.http.external

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.repository.InvalidNodeRefException
import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.module.core.applicationmodel.exception.NodesInconsistencyException
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaModel.PROPERTY_EXECUTION_IDS
import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.node.toNodeRefs
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.Retry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecutionResult
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.data.DataCleaner
import pl.beone.promena.alfresco.module.core.contract.node.*
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutorInjector
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutorValidator
import pl.beone.promena.alfresco.module.core.extension.*
import pl.beone.promena.core.applicationmodel.transformation.transformationDescriptor
import pl.beone.promena.core.contract.serialization.SerializationService
import pl.beone.promena.lib.connector.http.external.HttpPromenaTransformer
import pl.beone.promena.transformer.contract.communication.CommunicationParameters
import pl.beone.promena.transformer.contract.data.DataDescriptor
import pl.beone.promena.transformer.contract.data.TransformedDataDescriptor
import pl.beone.promena.transformer.contract.transformation.Transformation
import java.io.Serializable
import java.util.concurrent.Executors

/**
 * Provides HTTP bridge between Alfresco Content Services and Promena.
 * This implementation uses `promena-connector-http` connector module so it's required to include it on Promena.
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
class HttpPromenaTransformationExecutor(
    threads: Int,
    private val httpAddress: String,
    private val externalCommunicationParameters: CommunicationParameters,
    private val promenaMutableTransformationManager: PromenaTransformationManager.PromenaMutableTransformationManager,
    private val retry: Retry,
    private val postTransformationExecutorValidator: PostTransformationExecutorValidator,
    private val nodeInCurrentTransactionVerifier: NodeInCurrentTransactionVerifier,
    private val nodesChecksumGenerator: NodesChecksumGenerator,
    private val nodesExistenceVerifier: NodesExistenceVerifier,
    private val dataDescriptorGetter: DataDescriptorGetter,
    private val transformedDataDescriptorSaver: TransformedDataDescriptorSaver,
    private val dataCleaner: DataCleaner,
    private val postTransformationExecutorInjector: PostTransformationExecutorInjector,
    private val authorizationService: AuthorizationService,
    private val serviceRegistry: ServiceRegistry,
    serializationService: SerializationService
) : PromenaTransformationExecutor {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val httpPromenaTransformer = HttpPromenaTransformer(serializationService)
    private val coroutineScope = CoroutineScope(Executors.newFixedThreadPool(threads).asCoroutineDispatcher())

    override fun execute(
        transformation: Transformation,
        nodeDescriptor: NodeDescriptor,
        postTransformationExecutor: PostTransformationExecutor?,
        retry: Retry?
    ): TransformationExecution {
        val nodeRefs = nodeDescriptor.toNodeRefs()

        postTransformationExecutor?.let(postTransformationExecutorValidator::validate)
        nodeRefs.forEach(nodeInCurrentTransactionVerifier::verify)

        val nodesChecksum = nodesChecksumGenerator.generate(nodeRefs)
        val userName = authorizationService.getCurrentUser()

        val dataDescriptor = dataDescriptorGetter.get(nodeDescriptor)

        val transformationExecution = promenaMutableTransformationManager.startTransformation()
        addExecutionIdInNewTransaction(nodeRefs, transformationExecution.id)

        logger.start(transformation, nodeDescriptor)

        coroutineScope.launch {
            transform(
                Parameters(
                    transformation,
                    nodeDescriptor,
                    postTransformationExecutor,
                    determineRetry(retry),
                    dataDescriptor,
                    transformationExecution,
                    nodesChecksum,
                    userName
                ),
                0
            )
        }

        return transformationExecution
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

    private fun determineRetry(retry: Retry?): Retry =
        retry ?: this.retry

    private suspend fun transform(parameters: Parameters, attempt: Long) {
        val (transformation, _, _, _, dataDescriptor, _, _, _) = parameters

        try {
            val startTimestamp = getTimestamp()
            val performedTransformationDescriptor =
                httpPromenaTransformer.execute(transformationDescriptor(transformation, dataDescriptor, externalCommunicationParameters), httpAddress)
            val endTimestamp = getTimestamp()

            process(parameters) {
                processTransformedData(parameters, performedTransformationDescriptor.transformedDataDescriptor, startTimestamp, endTimestamp)
            }
        } catch (e: Exception) {
            process(parameters) {
                processException(parameters, attempt, e)
            }
        }
    }

    private suspend fun process(parameters: Parameters, toRunIfNodesExistAndHaveTheSameChecksum: suspend () -> Unit) {
        val (transformation, nodeDescriptor, _, _, _, transformationExecution, nodesChecksum, userName) = parameters
        val nodeRefs = nodeDescriptor.toNodeRefs()

        try {
            authorizationService.runAs(userName) { nodesExistenceVerifier.verify(nodeRefs) }

            val currentNodesChecksum = authorizationService.runAs(userName) { nodesChecksumGenerator.generate(nodeRefs) }
            if (nodesChecksum != currentNodesChecksum) {
                logger.stoppedTransformingBecauseChecksumsAreDifferent(transformation, nodeDescriptor, nodesChecksum, currentNodesChecksum)
                promenaMutableTransformationManager.completeErrorTransformation(
                    transformationExecution,
                    NodesInconsistencyException(nodeRefs, nodesChecksum, currentNodesChecksum)
                )
            } else {
                toRunIfNodesExistAndHaveTheSameChecksum()
            }
        } catch (e: InvalidNodeRefException) {
            logger.stoppedTransformingBecauseNodeDoesNotExist(transformation, nodeDescriptor, e.nodeRef)
            promenaMutableTransformationManager.completeErrorTransformation(transformationExecution, e)
        } catch (e: Exception) {
            logger.couldNotTransform(transformation, nodeDescriptor, e)
            promenaMutableTransformationManager.completeErrorTransformation(transformationExecution, e)
        }
    }

    private fun processTransformedData(
        parameters: Parameters,
        transformedDataDescriptor: TransformedDataDescriptor,
        startTimestamp: Long,
        endTimestamp: Long
    ) {
        val (transformation, nodeDescriptor, postTransformationExecutor, _, dataDescriptor, transformationExecution, _, userName) = parameters

        val transformationExecutionResult = authorizationService.runAs(userName) {
            serviceRegistry.retryingTransactionHelper.doInTransaction({
                transformedDataDescriptorSaver.save(
                    parameters.transformationExecution.id,
                    transformation,
                    nodeDescriptor.toNodeRefs(),
                    transformedDataDescriptor
                )
                    .let(::transformationExecutionResult)
                    .also { result ->
                        postTransformationExecutor
                            ?.also(postTransformationExecutorInjector::inject)
                            ?.execute(transformation, nodeDescriptor, result)

                        dataCleaner.clean(
                            dataDescriptor.descriptors.map(DataDescriptor.Single::data) +
                                    transformedDataDescriptor.descriptors.map(TransformedDataDescriptor.Single::data)
                        )
                    }
            }, false, true)
        }

        logger.transformedSuccessfully(transformation, nodeDescriptor, transformationExecutionResult, startTimestamp, endTimestamp)
        promenaMutableTransformationManager.completeTransformation(transformationExecution, transformationExecutionResult)
    }

    private suspend fun processException(parameters: Parameters, attempt: Long, exception: Exception) {
        val (transformation, nodeDescriptor, _, retry, _, transformationExecution, _, _) = parameters

        if (retry is Retry.No || wasLastAttempt(attempt, retry.maxAttempts)) {
            logger.couldNotTransform(transformation, nodeDescriptor, exception)
            promenaMutableTransformationManager.completeErrorTransformation(transformationExecution, exception)
        } else {
            val currentAttempt = attempt + 1

            logger.logOnRetry(transformation, nodeDescriptor, currentAttempt, retry.maxAttempts, retry.nextAttemptDelay, exception)
            delay(retry.nextAttemptDelay.toMillis())

            transform(parameters, currentAttempt)
        }
    }

    private fun wasLastAttempt(attempt: Long, retryMaxAttempts: Long): Boolean =
        attempt == retryMaxAttempts

    private fun getTimestamp(): Long =
        System.currentTimeMillis()
}
