package pl.beone.promena.alfresco.module.transformerrendition.predefined.configuration.transformer.definition

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.transformer.definition.DocumentPromenaContentTransformerDefinition

@Configuration
class DocumentPromenaContentTransformerDefinitionContext {

    @Bean
    fun documentPromenaContentTransformerDefinition() =
        DocumentPromenaContentTransformerDefinition
}