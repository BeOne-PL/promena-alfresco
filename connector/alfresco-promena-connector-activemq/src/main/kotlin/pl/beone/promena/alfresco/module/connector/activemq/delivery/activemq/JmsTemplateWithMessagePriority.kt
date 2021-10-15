package pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq

import groovy.util.logging.Slf4j
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.jms.core.MessagePostProcessor
import org.springframework.jms.support.JmsUtils
import org.springframework.util.Assert
import javax.jms.*

@Slf4j
class JmsTemplateWithMessagePriority : JmsTemplate {

    constructor(connectionFactory: ConnectionFactory) {
        super.setConnectionFactory(connectionFactory)
    }

    fun convertAndSend(priority: Int, destination: Destination, message: Any, postProcessor: MessagePostProcessor) {
        logger.warn("Send activemq message with priority $priority");
        this.send(priority, destination, MessageCreator { session: Session? ->
            val msg = this.messageConverter.toMessage(message, session)
            postProcessor.postProcessMessage(msg)
        })
    }

    private fun send(priority: Int, destination: Destination, messageCreator: MessageCreator) {
        this.execute<Any>({ session: Session ->
            this.doSend(priority, session, destination, messageCreator)
            null
        }, false)
    }

    private fun doSend(priority: Int, session: Session, destination: Destination?, messageCreator: MessageCreator) {
        Assert.notNull(messageCreator, "MessageCreator must not be null")
        val producer = createProducer(session, destination)
        try {
            val message = messageCreator.createMessage(session)
            if (logger.isDebugEnabled) {
                logger.debug("Sending created message: $message")
            }
            this.doSend(priority, producer, message)
            if (session.transacted && isSessionLocallyTransacted(session)) {
                JmsUtils.commitIfNecessary(session)
            }
        } finally {
            JmsUtils.closeMessageProducer(producer)
        }
    }


    fun doSend(priority: Int, producer: MessageProducer, message: Message?) {
        producer.priority = priority

        if (this.isExplicitQosEnabled) {
            producer.send(message, deliveryMode, priority, timeToLive)
        } else {
            producer.send(message)
        }
    }
}