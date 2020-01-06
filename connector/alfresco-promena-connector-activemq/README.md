# Promena Alfresco module - `alfresco-promena-activemq`
This module provide [`ActiveMQPromenaTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/connector/activemq/external/transformation/ActiveMQPromenaTransformationExecutor.kt) (`activeMQPromenaTransformationExecutor` bean name) implementation of [`PromenaTransformationExecutor`](./../../alfresco-promena-core/src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) interface based on ActiveMQ.

It implements the client side of [`promena-connector-activemq`](https://gitlab.office.beone.pl/promena/promena/tree/master/module/connector/activemq) connector module.

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.connector</groupId>
    <artifactId>alfresco-promena-connector-activemq</artifactId>
    <version>1.0.0</version>
</dependency>
```

## AMP
http://nexus.office.beone.pl/repository/releases/pl/beone/promena/alfresco/module/connector/alfresco-promena-connector-activemq/1.0.0/alfresco-promena-connector-activemq-1.0.0.amp

## Properties
```properties
promena.connector.activemq.consumer.queue.request=Promena.request
promena.connector.activemq.consumer.queue.response=Promena.response
promena.connector.activemq.consumer.queue.response.error=Promena.response.error

# Supported properties - the majority of https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/appendix-application-properties.html#integration-properties with prefix "promena.connector.activemq":
# - spring.jms.pub-sub-domain
# - spring.jms.listener.auto-startup
# - spring.jms.listener.acknowledge-mode
# - spring.jms.listener.concurrency
# - spring.jms.listener.max-concurrency
# - spring.jms.template.default-destination
# - spring.jms.template.delivery-delay
# - spring.jms.template.delivery-mode
# - spring.jms.template.priority
# - spring.jms.template.receive-timeout
# - spring.jms.template.time-to-live
# - spring.jms.template.qos-enabled
# - spring.activemq.broker-url
# - spring.activemq.close-timeout
# - spring.activemq.in-memory
# - spring.activemq.non-blocking-redelivery
# - spring.activemq.password
# - spring.activemq.send-timeout
# - spring.activemq.user
# - spring.activemq.packages.trust-all
# - spring.activemq.packages.trusted
promena.connector.activemq.spring.activemq.broker-url=${messaging.broker.url}
# The number of concurrent consumers (level of concurrency)
promena.connector.activemq.spring.jms.listener.concurrency=1
promena.connector.activemq.spring.jms.listener.max-concurrency=1
```