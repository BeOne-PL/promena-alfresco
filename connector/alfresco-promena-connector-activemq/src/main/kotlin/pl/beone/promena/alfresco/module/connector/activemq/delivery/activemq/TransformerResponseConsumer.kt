package pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq

import mu.KotlinLogging
import org.alfresco.service.ServiceRegistry
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.support.JmsHeaders.CORRELATION_ID
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_TRANSFORMATION_PARAMETERS
import pl.beone.promena.alfresco.module.connector.activemq.internal.TransformationParametersSerializationService
import pl.beone.promena.alfresco.module.core.applicationmodel.node.toNodeRefs
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecution
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecutionResult
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.data.DataCleaner
import pl.beone.promena.alfresco.module.core.contract.node.TransformedDataDescriptorSaver
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager.PromenaMutableTransformationManager
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutorInjector
import pl.beone.promena.alfresco.module.core.extension.transformedSuccessfully
import pl.beone.promena.connector.activemq.applicationmodel.PromenaJmsHeaders.TRANSFORMATION_END_TIMESTAMP
import pl.beone.promena.connector.activemq.applicationmodel.PromenaJmsHeaders.TRANSFORMATION_START_TIMESTAMP
import pl.beone.promena.core.applicationmodel.transformation.PerformedTransformationDescriptor
import pl.beone.promena.transformer.contract.data.DataDescriptor
import pl.beone.promena.transformer.contract.data.TransformedDataDescriptor

class TransformerResponseConsumer(
    private val promenaMutableTransformationManager: PromenaMutableTransformationManager,
    private val transformerResponseProcessor: TransformerResponseProcessor,
    private val transformedDataDescriptorSaver: TransformedDataDescriptorSaver,
    private val dataCleaner: DataCleaner,
    private val transformationParametersSerializationService: TransformationParametersSerializationService,
    private val postTransformationExecutorInjector: PostTransformationExecutorInjector,
    private val authorizationService: AuthorizationService,
    private val serviceRegistry: ServiceRegistry
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    /**
     * Serialization and deserialization of the body is executed by
     * [KryoMessageConverter][pl.beone.promena.connector.activemq.delivery.jms.message.converter.KryoMessageConverter] automatically.
     *
     * It carries out steps **2-8** of `The flow of an asynchronous transaction execution` ([ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor])
     *
     * @see [ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor]
     */
    @JmsListener(destination = "\${promena.connector.activemq.consumer.queue.response}")
    fun receiveQueue(
        @Header(CORRELATION_ID) executionId: String,
        @Header(TRANSFORMATION_START_TIMESTAMP) startTimestamp: Long,
        @Header(TRANSFORMATION_END_TIMESTAMP) endTimestamp: Long,
        @Header(SEND_BACK_TRANSFORMATION_PARAMETERS) transformationParameters: String,
        @Payload performedTransformationDescriptor: PerformedTransformationDescriptor
    ) {
        val transformationExecution = transformationExecution(executionId)

        val (transformation, nodeDescriptor, postTransformationExecutor, _, dataDescriptor, nodesChecksum, _, userName) =
            transformationParametersSerializationService.deserialize(transformationParameters)
        val transformedDataDescriptor = performedTransformationDescriptor.transformedDataDescriptor
        val nodeRefs = nodeDescriptor.toNodeRefs()

        transformerResponseProcessor.process(transformation, nodeDescriptor, transformationExecution, nodesChecksum, userName) {
            val transformationExecutionResult = authorizationService.runAs(userName) {
                serviceRegistry.retryingTransactionHelper.doInTransaction({
                    transformedDataDescriptorSaver.save(executionId, transformation, nodeRefs, transformedDataDescriptor)
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
    }
}