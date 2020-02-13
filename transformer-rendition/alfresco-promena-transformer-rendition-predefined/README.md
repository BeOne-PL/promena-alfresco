# Promena Alfresco module - `alfresco-promena-transformer-rendition-predefined`
See [Promena Alfresco - Development Guide#Definition](./../../DEVELOPMENT-GUIDE.md#definition) to find out how to provide a custom content transformer and a custom rendition.

## Definition
### Rendition
* `avatar32` ([`Avatar32PromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/image/Avatar32PromenaRenditionDefinition.kt))
* `avatar` ([`AvatarPromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/image/AvatarPromenaRenditionDefinition.kt))
* `imgpreview` ([`ImgPreviewPromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/image/ImgPreviewPromenaRenditionDefinition.kt))
* `doclib` ([`DocLibPromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/image/DocLibPromenaRenditionDefinition.kt))
* `medium` ([`MediumPromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/image/MediumPromenaRenditionDefinition.kt))
* `pdf` ([`PdfPromenaRenditionDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/rendition/definition/pdf/PdfPromenaRenditionDefinition.kt))

### Content Transformer
* Conversion between various document formats using [`converter - LibreOffice`](https://github.com/BeOne-PL/promena-transformer-converter-libreoffice) and [`converter - PDFBox`](https://github.com/BeOne-PL/promena-transformer-converter-pdfbox) transformers ([`DocumentPromenaContentTransformerDefinition`](src/main/kotlin/pl/beone/promena/alfresco/module/transformerrendition/predefined/internal/transformer/definition/DocumentPromenaContentTransformerDefinition.kt))

## Dependency
```xml
<dependency>
    <groupId>pl.beone.promena.alfresco.module.transformer-rendition</groupId>
    <artifactId>alfresco-promena-transformer-rendition-predefined</artifactId>
    <version>2.0.0</version>
</dependency>
```

## AMP
https://oss.sonatype.org/service/local/repositories/releases/content/pl/beone/promena/alfresco/module/transformer-rendition/alfresco-promena-transformer-rendition-predefined/2.0.0/alfresco-promena-transformer-rendition-predefined-2.0.0.amp