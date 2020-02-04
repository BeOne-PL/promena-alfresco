package pl.beone.promena.alfresco.lib.rendition.configuration.external.transformer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerDefinition
import pl.beone.promena.alfresco.lib.rendition.external.transformer.MemoryPromenaContentTransformerTransformationGetter

@Configuration
class MemoryPromenaContentTransformerDefinitionGetterContext {

    @Bean
    fun memoryPromenaContentTransformerDefinitionGetter(
        promenaContentTransformerDefinition: List<PromenaContentTransformerDefinition>
    ) =
        MemoryPromenaContentTransformerTransformationGetter(
            promenaContentTransformerDefinition
        )
}