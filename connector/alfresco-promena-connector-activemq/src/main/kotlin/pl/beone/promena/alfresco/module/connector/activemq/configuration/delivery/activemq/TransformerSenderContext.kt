package pl.beone.promena.alfresco.module.connector.activemq.configuration.delivery.activemq

import org.apache.activemq.command.ActiveMQQueue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MessageConverter
import pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq.JmsTemplateWithMessagePriority
import pl.beone.promena.alfresco.module.connector.activemq.delivery.activemq.TransformerSender
import pl.beone.promena.alfresco.module.connector.activemq.internal.TransformationParametersSerializationService
import pl.beone.promena.alfresco.module.core.extension.getRequiredPropertyWithResolvedPlaceholders
import java.util.*
import javax.jms.ConnectionFactory

@Configuration
class TransformerSenderContext {

    @Bean
    fun transformerSender(
        @Qualifier("global-properties") properties: Properties,
        transformationParametersSerializationService: TransformationParametersSerializationService,
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter
    ) : TransformerSender  {
        val jmsTemplateWithMessagePriority = JmsTemplateWithMessagePriority(connectionFactory)
        jmsTemplateWithMessagePriority.messageConverter = messageConverter
        jmsTemplateWithMessagePriority.isExplicitQosEnabled = true

        return TransformerSender(
            transformationParametersSerializationService,
            ActiveMQQueue(properties.getRequiredPropertyWithResolvedPlaceholders("promena.connector.activemq.consumer.queue.request")),
            jmsTemplateWithMessagePriority
        )
    }

}