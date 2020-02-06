package pl.beone.promena.alfresco.module.transformerrendition.predefined.configuration.rendition.definition.image

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.rendition.definition.image.DocLibPromenaRenditionDefinition

@Configuration
class DocLibPromenaRenditionDefinitionContext {

    @Bean
    fun docLibPromenaRenditionDefinition() =
        DocLibPromenaRenditionDefinition
}