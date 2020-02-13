package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition

import org.alfresco.repo.policy.PolicyComponent
import org.alfresco.service.cmr.repository.NodeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.ThumbnailNodeCreationBehaviour

@Configuration
class ThumbnailNodeCreationBehaviourContext {

    @Bean
    fun thumbnailNodeCreationBehaviour(
        policyComponent: PolicyComponent,
        nodeService: NodeService
    ) =
        ThumbnailNodeCreationBehaviour(
            policyComponent,
            nodeService
        )
}