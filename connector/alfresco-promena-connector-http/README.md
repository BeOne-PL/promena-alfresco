# Promena Alfresco connector module - `alfresco-promena-connector-http`
This module provide [`HttpPromenaTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/connector/http/external/HttpPromenaTransformationExecutor.kt) (`httpPromenaTransformationExecutor` bean name) implementation of [`PromenaTransformationExecutor`](./../../alfresco-promena-core/src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) interface based on HTTP.

It implements the client side of [`promena-connector-http`](https://github.com/BeOne-PL/promena/tree/master/module/connector/http) connector module.

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.connector</groupId>
    <artifactId>alfresco-promena-connector-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

## AMP
https://oss.sonatype.org/service/local/repositories/releases/content/pl/beone/promena/alfresco/module/connector/alfresco-promena-connector-http/1.0.0/alfresco-promena-connector-http-1.0.0.amp

## Properties
```properties
promena.connector.http.host=promena
promena.connector.http.port=8080
# Number of execution threads (level of concurrency)
promena.connector.http.execution.threads=1
```