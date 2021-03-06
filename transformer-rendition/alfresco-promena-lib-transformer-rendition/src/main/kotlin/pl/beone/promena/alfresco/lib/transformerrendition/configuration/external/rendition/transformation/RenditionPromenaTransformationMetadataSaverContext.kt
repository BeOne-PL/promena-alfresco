package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition.transformation

import org.alfresco.service.ServiceRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.transformation.RenditionPromenaTransformationMetadataSaver

@Configuration
class RenditionPromenaTransformationMetadataSaverContext {

    @Bean
    fun renditionPromenaTransformationMetadataSaver(
        serviceRegistry: ServiceRegistry
    ) =
        RenditionPromenaTransformationMetadataSaver(
            serviceRegistry
        )
}