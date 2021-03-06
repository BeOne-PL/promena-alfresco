package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition

import org.alfresco.service.cmr.repository.NodeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.PromenaRenditionGetter

@Configuration
class PromenaRenditionGetterContext {

    @Bean
    fun promenaRenditionGetter(
        nodeService: NodeService
    ) =
        PromenaRenditionGetter(
            nodeService
        )
}