package pl.beone.promena.alfresco.module.transformerrendition.predefined.configuration.rendition.definition.pdf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.rendition.definition.pdf.PdfPromenaRenditionDefinition

@Configuration
class PdfPromenaRenditionDefinitionContext {

    @Bean
    fun pdfPromenaRenditionDefinition() =
        PdfPromenaRenditionDefinition()
}