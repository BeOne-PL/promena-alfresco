# Promena Alfresco module - `alfresco-promena-rendition_6.2.0`
This module replaces standard Alfresco rendition system with the equivalent in Promena environment. It also disables `Transformers` subsystem and everything around rendition (by replacing beans with empty beans).

If you want to provide a custom rendition you have to implement [`PromenaRenditionDefinition`](./../alfresco-promena-lib-rendition/src/main/kotlin/pl/beone/promena/alfresco/lib/rendition/contract/definition/PromenaRenditionDefinition.kt) (see [Promena Alfresco - Development Guide](./DEVELOPMENT-GUIDE.md) to find out how to do it).

This version of `alfresco-promena-rendition` module is suited to Alfresco Content Services 6.2.0. Visit [`alfresco-promena-rendition_6.1.2`](./../alfresco-promena-rendition_6.1.2) for version compatible with Alfresco Content Services 6.1.2.

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.rendition</groupId>
    <artifactId>alfresco-promena-rendition_6.2.0</artifactId>
    <version>1.0.0</version>
</dependency>
```

## AMP
http://nexus.office.beone.pl/repository/releases/pl/beone/promena/alfresco/module/rendition/alfresco-promena-rendition_6.2.0/1.0.0/alfresco-promena-rendition_6.2.0-1.0.0.amp

## Properties
```properties
# Maximum time to complete a transformation
promena.rendition.transformation.timeout=10m
# The bean name of a executor
#  if you have only one connector installed, you can leave it empty
#  if you have more than one connector installed, you have to specify the name manually
promena.rendition.transformer.bean.name=
```