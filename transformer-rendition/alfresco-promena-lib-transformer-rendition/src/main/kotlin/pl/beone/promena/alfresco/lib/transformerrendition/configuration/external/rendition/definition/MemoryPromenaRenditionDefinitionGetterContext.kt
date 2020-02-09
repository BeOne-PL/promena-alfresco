package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition.definition

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.definition.MemoryPromenaRenditionDefinitionGetter

@Configuration
class MemoryPromenaRenditionDefinitionGetterContext {

    @Bean
    fun memoryPromenaRenditionDefinitionGetter(
        promenaRenditionDefinitions: List<PromenaRenditionDefinition>
    ) =
        MemoryPromenaRenditionDefinitionGetter(
            promenaRenditionDefinitions
        )
}