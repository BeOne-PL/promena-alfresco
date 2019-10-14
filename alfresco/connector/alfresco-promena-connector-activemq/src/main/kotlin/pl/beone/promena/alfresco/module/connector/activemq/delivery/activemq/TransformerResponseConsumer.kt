package pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq

import mu.KotlinLogging
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.support.JmsHeaders.CORRELATION_ID
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_TRANSFORMATION_PARAMETERS
import pl.beone.promena.alfresco.module.connector.activemq.internal.ReactiveTransformationManager
import pl.beone.promena.alfresco.module.connector.activemq.internal.TransformationParametersSerializationService
import pl.beone.promena.alfresco.module.core.applicationmodel.exception.AnotherTransformationIsInProgressException
import pl.beone.promena.alfresco.module.core.applicationmodel.node.toNodeRefs
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.NodesChecksumGenerator
import pl.beone.promena.alfresco.module.core.contract.TransformedDataDescriptorSaver
import pl.beone.promena.alfresco.module.core.extension.skippedSavingResult
import pl.beone.promena.alfresco.module.core.extension.transformedSuccessfully
import pl.beone.promena.connector.activemq.applicationmodel.PromenaJmsHeaders.TRANSFORMATION_END_TIMESTAMP
import pl.beone.promena.connector.activemq.applicationmodel.PromenaJmsHeaders.TRANSFORMATION_START_TIMESTAMP
import pl.beone.promena.core.applicationmodel.transformation.PerformedTransformationDescriptor

class TransformerResponseConsumer(
    private val nodesChecksumGenerator: NodesChecksumGenerator,
    private val transformedDataDescriptorSaver: TransformedDataDescriptorSaver,
    private val authorizationService: AuthorizationService,
    private val reactiveTransformationManager: ReactiveTransformationManager,
    private val transformationParametersSerializationService: TransformationParametersSerializationService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @JmsListener(destination = "\${promena.connector.activemq.consumer.queue.response}")
    fun receiveQueue(
        @Header(CORRELATION_ID) correlationId: String,
        @Header(TRANSFORMATION_START_TIMESTAMP) startTimestamp: Long,
        @Header(TRANSFORMATION_END_TIMESTAMP) endTimestamp: Long,
        @Header(SEND_BACK_TRANSFORMATION_PARAMETERS) transformationParameters: String,
        @Payload performedTransformationDescriptor: PerformedTransformationDescriptor
    ) {
        val (nodeDescriptors, nodesChecksum, _, _, userName) = transformationParametersSerializationService.deserialize(transformationParameters)
        val nodeRefs = nodeDescriptors.toNodeRefs()

        val (transformation, transformedDataDescriptors) = performedTransformationDescriptor

        val currentNodesChecksum = nodesChecksumGenerator.generateChecksum(nodeRefs)
        if (nodesChecksum != currentNodesChecksum) {
            reactiveTransformationManager.completeErrorTransformation(
                correlationId,
                AnotherTransformationIsInProgressException(
                    transformation,
                    nodeDescriptors,
                    nodesChecksum,
                    currentNodesChecksum
                )
            )

            logger.skippedSavingResult(transformation, nodeDescriptors, nodesChecksum, currentNodesChecksum)
        } else {
            val targetNodeRefs = authorizationService.runAs(userName) {
                transformedDataDescriptorSaver.save(transformation, nodeRefs, transformedDataDescriptors)
            }
            reactiveTransformationManager.completeTransformation(correlationId, targetNodeRefs)

            logger.transformedSuccessfully(transformation, nodeDescriptors, targetNodeRefs, startTimestamp, endTimestamp)
        }
    }
}