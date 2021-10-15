package pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq

import org.apache.activemq.command.ActiveMQQueue
import org.springframework.jms.JmsException
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.jms.core.MessagePostProcessor
import org.springframework.jms.core.SessionCallback
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_ATTEMPT
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_RETRY_MAX_ATTEMPTS
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_TRANSFORMATION_PARAMETERS
import pl.beone.promena.alfresco.module.connector.activemq.applicationmodel.PromenaJmsHeaders.SEND_BACK_TRANSFORMATION_PARAMETERS_STRING
import pl.beone.promena.alfresco.module.connector.activemq.external.transformation.TransformationParameters
import pl.beone.promena.alfresco.module.connector.activemq.internal.TransformationParametersSerializationService
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.Retry
import pl.beone.promena.core.applicationmodel.transformation.TransformationDescriptor

class TransformerSender(
    private val transformationParametersSerializationService: TransformationParametersSerializationService,
    private val queueRequest: ActiveMQQueue,
    private val jmsTemplate: JmsTemplateWithMessagePriority
) {

    /**
     * Serialization and deserialization of the body is executed by
     * [KryoMessageConverter][pl.beone.promena.connector.activemq.delivery.jms.message.converter.KryoMessageConverter] automatically.
     *
     * It carries out step **1** of `The flow of an asynchronous transaction execution` ([ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor])
     * Determines headers to send first:
     *  [SEND_BACK_ATTEMPT] the number of a current attempt [transformationParameters.attempt]
     *  [SEND_BACK_RETRY_MAX_ATTEMPTS] the number of max attempts [transformationParameters.maxAttempts]
     *  [SEND_BACK_TRANSFORMATION_PARAMETERS] with serialized [transformationParameters]
     *  and [SEND_BACK_TRANSFORMATION_PARAMETERS_STRING] with stringified [transformationParameters].
     * Then, sends a message with serialized data to as the body with [executionId] id to [queueRequest] queue.
     *
     * @see [ActiveMQPromenaTransformationExecutor][pl.beone.promena.alfresco.module.connector.activemq.external.transformation.ActiveMQPromenaTransformationExecutor]
     */
    fun send(executionId: String, transformationDescriptor: TransformationDescriptor, transformationParameters: TransformationParameters, priority: Int = 4) {

        jmsTemplate.convertAndSend(priority, queueRequest, transformationDescriptor, MessagePostProcessor { message ->
            message.apply {
                jmsCorrelationID = executionId

                setStringProperty(SEND_BACK_TRANSFORMATION_PARAMETERS, transformationParametersSerializationService.serialize(transformationParameters))
                setStringProperty(SEND_BACK_TRANSFORMATION_PARAMETERS_STRING, transformationParameters.toString())

                setLongProperty(SEND_BACK_ATTEMPT, transformationParameters.attempt)
                setLongProperty(SEND_BACK_RETRY_MAX_ATTEMPTS, determineMaxAttempts(transformationParameters))
            }
        });

    }

    private fun determineMaxAttempts(transformationParameters: TransformationParameters): Long =
        if (transformationParameters.retry !is Retry.No) {
            transformationParameters.retry.maxAttempts
        } else {
            0
        }
}