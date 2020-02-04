package pl.beone.promena.alfresco.lib.rendition.configuration.external.definition

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.rendition.contract.definition.PromenaRenditionDefinition
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