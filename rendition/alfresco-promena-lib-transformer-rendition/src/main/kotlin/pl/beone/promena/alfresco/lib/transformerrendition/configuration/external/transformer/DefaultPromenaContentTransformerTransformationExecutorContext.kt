package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.transformer

import org.alfresco.service.ServiceRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.getPromenaTransformationExecutor
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.external.transformer.DefaultPromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.alfresco.module.core.extension.getPropertyWithEmptySupport
import pl.beone.promena.alfresco.module.core.extension.getRequiredPropertyWithResolvedPlaceholders
import pl.beone.promena.alfresco.module.core.extension.toDuration
import java.util.*

@Configuration
class DefaultPromenaContentTransformerTransformationExecutorContext {

    @Bean
    fun defaultPromenaContentTransformerTransformationExecutor(
        applicationContext: ApplicationContext,
        @Qualifier("global-properties") properties: Properties,
        serviceRegistry: ServiceRegistry,
        promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter,
        promenaTransformationManager: PromenaTransformationManager
    ) =
        DefaultPromenaContentTransformerTransformationExecutor(
            serviceRegistry,
            promenaContentTransformerDefinitionGetter,
            applicationContext.getPromenaTransformationExecutor(properties.getPropertyWithEmptySupport("promena.transformer-rendition.rendition.transformer.bean.name")),
            promenaTransformationManager,
            properties.getRequiredPropertyWithResolvedPlaceholders("promena.transformer-rendition.rendition.transformation.timeout").toDuration()
        )
}