package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.transformer.definition

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinition
import javax.annotation.PostConstruct

@Configuration
class PromenaContentTransformerDefinitionLogger(
    private val promenaContentTransformerDefinitions: List<PromenaContentTransformerDefinition>
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostConstruct
    private fun log() {
        logger.info {
            "Found <${promenaContentTransformerDefinitions.size}> content transformer definition(s): " + "${promenaContentTransformerDefinitions.map { it::class.simpleName }}"
        }
    }
}