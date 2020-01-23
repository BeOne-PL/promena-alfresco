# Promena Alfresco module - `alfresco-promena-core`
This module provides core functionality that allows you to integrate Alfresco Content Services with Promena. It contains a lot of interfaces and classes (see [Promena Alfresco - Development Guide](./../DEVELOPMENT-GUIDE.md) to find out how to use them) but from the user point of view two of them are important:
* [`PromenaTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) interface to perform a transformation
* [`PromenaTransformationManager`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationManager.kt) interface [[*`MemoryWithAlfrescoPersistencePromenaMutableTransformationManager`*](./src/main/kotlin/pl/beone/promena/alfresco/module/core/external/transformation/manager/MemoryWithAlfrescoPersistencePromenaMutableTransformationManager.kt) implementation] to wait for the result of a transformation execution

No implementation of [`PromenaTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) is available in this module because it depends on a connector - you have to install at least one connector to integrate Alfresco Content Services with Promena.

Every transformation execution is asynchronous and provides retrying mechanism. You can specify parameters of retrying mechanism in `alfresco-global.properties` globally or for each execution locally (passed as a parameter). 

You can implement [`PromenaTransformationMetadataSaver`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationMetadataSaver.kt) to provide custom metadata saver that will be run after a transformation.

## Flow
1. Inject a implementation of [`PromenaTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) and execute the transformation
2. [`TransformationExecution`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/applicationmodel/transformation/TransformationExecution.kt) is returned and added to `promena:executionIds` property in nodes from [`NodeDescriptor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/applicationmodel/node/NodeDescriptor.kt)
3. The result of the transformation is saved in Alfresco Content Services in the following format (provided by [`MinimalRenditionTransformedDataDescriptorSaver`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/external/node/MinimalRenditionTransformedDataDescriptorSaver.kt) implementation):
    * Each [`TransformedDataDescriptor`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/data/TransformedDataDescriptor.kt) is saved as the child association (`rn:rendition`) in nodes from [`NodeDescriptor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/applicationmodel/node/NodeDescriptor.kt) with type `cm:thumbnail` and properties:
        * `cm:content` - [`Data`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/model/data/Data.kt) of [`TransformedDataDescriptor`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/data/TransformedDataDescriptor.kt) element
        * `promena:executionId` - the transformation execution id
        * `promena:transformationId` - the array of [`TransformerId`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/transformer/TransformerId.kt) elements
        * `promena:transformationDataIndex` - the index of [`TransformedDataDescriptor`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/data/TransformedDataDescriptor.kt) element
        * `promena:transformationDataSize` - the number of [`TransformedDataDescriptor`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/data/TransformedDataDescriptor.kt) elements
        * `promena:transformation` - the array of `toString` executed on [`Transformation`](https://github.com/BeOne-PL/promena/blob/master/base/promena-transformer/contract/src/main/kotlin/pl/beone/promena/transformer/contract/transformation/Transformation.kt) elements
4. *Optional.* [`PostTransformationExecutor`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationExecutor.kt) is run if it was set
5. Registered implementations of [`PromenaTransformationMetadataSaver`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationMetadataSaver.kt) are run
4. In case of an error, another execution is run until the number of [`Retry`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/applicationmodel/retry/Retry.kt) (`maxAttempts`) (from the function parameter if it was set or from global parameters) is reached
6. *Optional.* Get the result of the transformation using [`PromenaTransformationManager`](./src/main/kotlin/pl/beone/promena/alfresco/module/core/contract/transformation/PromenaTransformationManager.kt)

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module</groupId>
    <artifactId>alfresco-promena-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## AMP
https://oss.sonatype.org/service/local/repo_groups/public/content/pl/beone/promena/alfresco/module/alfresco-promena-core/1.0.0/alfresco-promena-core-1.0.0.amp

## Properties
```properties
## External communication
# Type: memory or file
promena.core.communication.external.id=memory
# Directory where data for file communication are saved
promena.core.communication.external.file.directory.path=

## Retrying mechanism
promena.core.transformation.error.retry.enabled=true
# Max attempts
promena.core.transformation.error.retry.max-attempts=20
# Delay before next attempt of transformation execution
promena.core.transformation.error.retry.next-attempt-delay=15s

## Transformation manager
# Specifies if history of transformation executions should be persisted (required if you want to get results of transformation execution after Alfresco Content Services restart)
promena.core.transformation.manager.persist-in-alfresco=true
# Size of memory buffer
promena.core.transformation.manager.buffer-size=1000
# Maximum waiting time for the end of transformation and result 
promena.core.transformation.manager.wait-max=1m

# Empty node is created if there is no result of transformation
promena.core.transformation.save-if-zero=true
```