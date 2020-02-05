package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition

import org.alfresco.service.ServiceRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.getPromenaTransformationExecutor
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.PromenaRenditionInProgressSynchronizer
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.RenditionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinitionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.DefaultPromenaRenditionTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.alfresco.module.core.extension.getPropertyWithEmptySupport
import pl.beone.promena.alfresco.module.core.extension.getRequiredPropertyWithResolvedPlaceholders
import pl.beone.promena.alfresco.module.core.extension.toDuration
import java.util.*

@Configuration
class DefaultPromenaRenditionTransformationExecutorContext {

    @Bean
    fun defaultPromenaRenditionTransformationExecutor(
        applicationContext: ApplicationContext,
        @Qualifier("global-properties") properties: Properties,
        serviceRegistry: ServiceRegistry,
        renditionGetter: RenditionGetter,
        promenaRenditionDefinitionGetter: PromenaRenditionDefinitionGetter,
        promenaRenditionInProgressSynchronizer: PromenaRenditionInProgressSynchronizer,
        promenaTransformationManager: PromenaTransformationManager,
        promenaTransformationExecutors: List<PromenaTransformationExecutor>
    ) =
        DefaultPromenaRenditionTransformationExecutor(
            serviceRegistry,
            renditionGetter,
            promenaRenditionDefinitionGetter,
            promenaRenditionInProgressSynchronizer,
            applicationContext.getPromenaTransformationExecutor(properties.getPropertyWithEmptySupport("promena.transformer-rendition.content-transformer.transformer.bean.name")),
            promenaTransformationManager,
            properties.getRequiredPropertyWithResolvedPlaceholders("promena.transformer-rendition.transformer.transformation.timeout").toDuration()
        )
}