package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.transformer

import org.alfresco.service.ServiceRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.external.transformer.DefaultPromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.alfresco.module.core.extension.getPropertyWithEmptySupport
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
            applicationContext.getPromenaTransformationExecutor(properties.getPropertyWithEmptySupport("promena.rendition.transformer.bean.name")),
            promenaTransformationManager
        )

    private fun ApplicationContext.getPromenaTransformationExecutor(beanName: String?): PromenaTransformationExecutor =
        if (beanName != null) {
            getBean(beanName, PromenaTransformationExecutor::class.java)
        } else {
            getBean(PromenaTransformationExecutor::class.java)
        }
}