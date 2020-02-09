package pl.beone.promena.alfresco.module.transformerrendition.predefined.configuration.rendition.definition.image

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.rendition.definition.image.ImgPreviewPromenaRenditionDefinition

@Configuration
class ImgPreviewPromenaRenditionDefinitionContext {

    @Bean
    fun imgPreviewPromenaRenditionDefinition() =
        ImgPreviewPromenaRenditionDefinition
}