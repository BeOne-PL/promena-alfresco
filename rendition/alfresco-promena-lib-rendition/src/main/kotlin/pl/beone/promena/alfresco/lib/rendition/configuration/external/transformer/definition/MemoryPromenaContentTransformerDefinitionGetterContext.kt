package pl.beone.promena.alfresco.lib.rendition.configuration.external.transformer.definition

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.definition.PromenaContentTransformerDefinition
import pl.beone.promena.alfresco.lib.rendition.external.transformer.definition.MemoryPromenaContentTransformerDefinitionGetter

@Configuration
class MemoryPromenaContentTransformerDefinitionGetterContext {

    @Bean
    fun memoryPromenaContentTransformerDefinitionGetter(
        promenaContentTransformerDefinitions: List<PromenaContentTransformerDefinition>
    ) =
        MemoryPromenaContentTransformerDefinitionGetter(
            promenaContentTransformerDefinitions
        )
}