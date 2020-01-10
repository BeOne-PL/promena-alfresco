# Promena Alfresco
This repository contains a set of modules that help integrate Alfresco with Promena.

## Module
### [`alfresco-promena-core`](./alfresco-promena-core)
Contains [`PromenaTransformationExecutor`](./alfresco-promena-core/src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) (to perform a transformation) and [`PromenaTransformationManager`](./alfresco-promena-core/src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationManager.kt) (to wait for the result of a transformation execution). It also contains a set of interfaces and classes that help implement custom solutions (a connector for example).

### [`alfresco-promena-connector-activemq`](./connector/alfresco-promena-connector-activemq)
Provides [`ActiveMQPromenaTransformationExecutor`](./connector/alfresco-promena-connector-activemq/src/main/kotlin/pl/beone/promena/alfresco/module/connector/activemq/external/transformation/ActiveMQPromenaTransformationExecutor.kt) based on ActiveMQ.

### [`alfresco-promena-connector-http`](./connector/alfresco-promena-connector-http)
Provides [`HttpPromenaTransformationExecutor`](./connector/alfresco-promena-connector-http/src/main/kotlin/pl/beone/promena/alfresco/module/connector/http/external/HttpPromenaTransformationExecutor.kt) based on HTTP.

### [`alfresco-promena-rendition_6.1.2`](./rendition/alfresco-promena-rendition_6.1.2) & [`alfresco-promena-rendition_6.2.0`](./rendition/alfresco-promena-rendition_6.2.0)
Replaces the standard Alfresco rendition system with the equivalent in Promena environment. The first one is for Alfresco Content Services 6.1.2 and the second one is for Alfresco Content Services 6.2.0.

### [`alfresco-promena-predefined-rendition`](./rendition/alfresco-promena-predefined-rendition)
Provides `avatar32`, `avatar`, `imgpreview`, `doclib`, `medium` and `pdf` renditions.

## [Development Guide](./DEVELOPMENT-GUIDE.md)
Explains and demonstrates how to implement custom solutions such as a connector, a rendition or a metadata saver.

## [Sample#Alfresco](https://gitlab.office.beone.pl/promena/promena-sample#alfresco)
There are several samples that demonstrate how to integrate Alfresco with Promena divided into 3 categories: communication, connector and rendition.