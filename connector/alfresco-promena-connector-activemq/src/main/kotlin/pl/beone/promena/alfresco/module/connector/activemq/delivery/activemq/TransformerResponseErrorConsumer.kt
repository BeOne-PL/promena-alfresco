package pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq

import mu.KotlinLogging
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.support.JmsHeaders.CORRELATION_ID
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_TRANSFORMATION_PARAMETERS
import pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor
import pl.beone.promena.alfresco.module.connector.activemq.internal.TransformationParametersSerializationService
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.Retry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecution
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager.PromenaMutableTransformationManager
import pl.beone.promena.alfresco.module.core.extension.couldNotTransform
import pl.beone.promena.alfresco.module.core.extension.logOnRetry
import pl.beone.promena.core.applicationmodel.exception.transformation.TransformationException

class TransformerResponseErrorConsumer(
    private val promenaMutableTransformationManager: PromenaMutableTransformationManager,
    private val transformerResponseProcessor: TransformerResponseProcessor,
    private val activeMQPromenaTransformer: ActiveMQPromenaTransformationExecutor,
    private val authorizationService: AuthorizationService,
    private val transformationParametersSerializationService: TransformationParametersSerializationService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    /**
     * Listens to `${promena.connector.activemq.consumer.queue.response.error}` queue and reacts to the failed result of the transformation execution.
     * It only listens to messages that meet `${promena.connector.activemq.consumer.queue.response.error.selector}` condition.
     */
    @JmsListener(
        destination = "\${promena.connector.activemq.consumer.queue.response.error}",
        selector = "\${promena.connector.activemq.consumer.queue.response.error.selector}"
    )
    fun receiveQueue(
        @Header(CORRELATION_ID) executionId: String,
        @Header(SEND_BACK_TRANSFORMATION_PARAMETERS) transformationParametersString: String,
        @Payload transformationException: TransformationException
    ) {
        val transformationExecution = transformationExecution(executionId)

        val transformationParameters = transformationParametersSerializationService.deserialize(transformationParametersString)
        val (transformation, nodeDescriptor, _, retry, dataDescriptor, nodesChecksum, attempt, userName) = transformationParameters

        transformerResponseProcessor.process(transformation, nodeDescriptor, transformationExecution, nodesChecksum, userName) {
            if (retry is Retry.No || wasLastAttempt(attempt, retry.maxAttempts)) {
                logger.couldNotTransform(transformation, nodeDescriptor, transformationException)
                promenaMutableTransformationManager.completeErrorTransformation(transformationExecution, transformationException)
            } else {
                val currentAttempt = attempt + 1

                logger.logOnRetry(transformation, nodeDescriptor, currentAttempt, retry.maxAttempts, retry.nextAttemptDelay, transformationException)
                Thread.sleep(retry.nextAttemptDelay.toMillis())

                authorizationService.runAs(userName) {
                    activeMQPromenaTransformer.execute(
                        executionId,
                        transformation,
                        dataDescriptor,
                        transformationParameters.copy(attempt = currentAttempt)
                    )
                }
            }
        }
    }

    private fun wasLastAttempt(attempt: Long, retryMaxAttempts: Long): Boolean =
        attempt == retryMaxAttempts
}