package pl.beone.promena.alfresco.lib.rendition.configuration.external.rendition

import org.alfresco.service.ServiceRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.rendition.contract.rendition.PromenaRenditionInProgressSynchronizer
import pl.beone.promena.alfresco.lib.rendition.contract.rendition.RenditionGetter
import pl.beone.promena.alfresco.lib.rendition.contract.rendition.definition.PromenaRenditionDefinitionGetter
import pl.beone.promena.alfresco.lib.rendition.external.rendition.DefaultPromenaRenditionTransformationExecutor
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
        promenaTransformationManager: PromenaTransformationManager
    ) =
        DefaultPromenaRenditionTransformationExecutor(
            serviceRegistry,
            renditionGetter,
            promenaRenditionDefinitionGetter,
            promenaRenditionInProgressSynchronizer,
            applicationContext.getPromenaTransformationExecutor(properties.getPropertyWithEmptySupport("promena.rendition.transformer.bean.name")),
            promenaTransformationManager,
            properties.getRequiredPropertyWithResolvedPlaceholders("promena.rendition.transformation.timeout").toDuration()
        )

    private fun ApplicationContext.getPromenaTransformationExecutor(beanName: String?): PromenaTransformationExecutor =
        if (beanName != null) {
            getBean(beanName, PromenaTransformationExecutor::class.java)
        } else {
            getBean(PromenaTransformationExecutor::class.java)
        }
}