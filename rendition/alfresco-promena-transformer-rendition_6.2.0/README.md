# Promena Alfresco module - `alfresco-promena-transformer-rendition_6.2.0`
This module replaces the standard Alfresco rendition system with the equivalent in Promena environment. It also disables `Transformers` subsystem and everything around rendition (by replacing beans with empty beans).

If you want to provide a custom rendition you have to implement [`PromenaRenditionDefinition`](../alfresco-promena-lib-transformer-rendition/src/main/kotlin/pl/beone/promena/alfresco/lib/rendition/contract/rendition/definition/PromenaRenditionDefinition.kt) (visit [Promena Alfresco - Development Guide](./../../DEVELOPMENT-GUIDE.md) to find out how to do it).

This version of `alfresco-promena-rendition` module is suited to Alfresco Content Services 6.2.0. Visit [`alfresco-promena-rendition_6.1.2`](./../alfresco-promena-rendition_6.1.2) for version compatible with Alfresco Content Services 6.1.2.

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.transformer-rendition</groupId>
    <artifactId>alfresco-promena-transformer-rendition_6.2.0</artifactId>
    <version>2.0.0</version>
</dependency>
```

## AMP
https://oss.sonatype.org/service/local/repo_groups/public/content/pl/beone/promena/alfresco/module/rendition/alfresco-promena-transformer-rendition_6.2.0/1.0.0/alfresco-promena-transformer-rendition_6.2.0-1.0.0.amp

## Properties
```properties
# Maximum time to complete transformation
promena.transformer-rendition.rendition.transformation.timeout=10m
# Bean name of executor
#  if you have only one connector installed, you can leave it empty
#  if you have more than one connector installed, you have to specify name manually
promena.transformer-rendition.rendition.transformer.bean.name=
```