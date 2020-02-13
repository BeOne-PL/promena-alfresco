# Promena Alfresco module - `alfresco-promena-transformer-rendition_6.2.0`
This module replaces the standard Alfresco Transformer and Rendition system with the equivalent in Promena environment.

If you want to provide a custom rendition you have to implement [`PromenaRenditionDefinition`](../alfresco-promena-lib-transformer-rendition/src/main/kotlin/pl/beone/promena/alfresco/lib/transformerrendition/contract/rendition/definition/PromenaRenditionDefinition.kt) (visit [Promena Alfresco - Development Guide#Definition/Rendition](./../../DEVELOPMENT-GUIDE.md#rendition) to find out how to do it).

If you want to provide a custom content transformer you have to implement [`PromenaContentTransformerDefinition`](../alfresco-promena-lib-transformer-rendition/src/main/kotlin/pl/beone/promena/alfresco/lib/transformerrendition/contract/transformer/definition/PromenaContentTransformerDefinition.kt) (visit [Promena Alfresco - Development Guide#Definition/Content Transformer](./../../DEVELOPMENT-GUIDE.md#content-transformer) to find out how to do it).

This version of `alfresco-promena-transformer-rendition` module is suited to Alfresco Content Services 6.2.0. Visit [`alfresco-promena-transformer-rendition_6.1.2`](./../alfresco-promena-transformer-rendition_6.1.2) for version compatible with Alfresco Content Services 6.1.2.

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.transformer-rendition</groupId>
    <artifactId>alfresco-promena-transformer-rendition_6.2.0</artifactId>
    <version>2.0.1</version>
</dependency>
```

## AMP
https://oss.sonatype.org/service/local/repositories/releases/content/pl/beone/promena/alfresco/module/transformer-rendition/alfresco-promena-transformer-rendition_6.2.0/2.0.1/alfresco-promena-transformer-rendition_6.2.0-2.0.1.amp

## Properties
```properties
# Maximum time to complete transformation
## Rendition
promena.transformer-rendition.rendition.transformation.timeout=10m
## Content Transformer
promena.transformer-rendition.transformer.transformation.timeout=10m
# Bean name of executor
#  if you have only one connector installed, you can leave it empty
#  if you have more than one connector installed, you have to specify name manually
## Rendition
promena.transformer-rendition.rendition.transformer.bean.name=
## Content Transformer
promena.transformer-rendition.content-transformer.transformer.bean.name=
```