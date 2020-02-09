package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external.rendition.definition

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition
import javax.annotation.PostConstruct

@Configuration
class PromenaRenditionDefinitionLogger(
    private val promenaRenditionDefinitions: List<PromenaRenditionDefinition>
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostConstruct
    private fun log() {
        logger.info {
            "Found <${promenaRenditionDefinitions.size}> rendition definition(s): " + "${promenaRenditionDefinitions.map { it::class.simpleName }}"
        }
    }
}