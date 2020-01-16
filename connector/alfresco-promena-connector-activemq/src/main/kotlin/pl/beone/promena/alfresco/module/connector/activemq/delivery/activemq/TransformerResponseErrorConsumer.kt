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
     * Serialization and deserialization of the body is executed by
     * [KryoMessageConverter][pl.beone.promena.connector.activemq.delivery.jms.message.converter.KryoMessageConverter] automatically.
     *
     * It carries out **8** point of `The flow of asynchronous transaction execution` ([ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor])
     * Listens to `${promena.connector.activemq.consumer.queue.response.error}` queue and reacts to the failed result of a transformation execution.
     * Another execution is run until the number of [Retry.maxAttempts] is reached.
     * `${promena.connector.activemq.consumer.queue.response.error.selector}` selector guarantees that
     * this consumer only receives messages meeting this requirement.
     * If the number is reached, it completes the transformation execution with an exception ([promenaMutableTransformationManager])
     *
     * @see [ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor]
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