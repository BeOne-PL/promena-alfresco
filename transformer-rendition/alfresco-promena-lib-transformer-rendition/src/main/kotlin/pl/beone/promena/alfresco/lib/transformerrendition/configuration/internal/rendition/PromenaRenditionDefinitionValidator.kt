package pl.beone.promena.alfresco.lib.transformerrendition.configuration.internal.rendition

import org.springframework.stereotype.Component
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinitionGetter
import javax.annotation.PostConstruct

@Component
class PromenaRenditionDefinitionValidator(
    private val promenaRenditionDefinitionGetter: PromenaRenditionDefinitionGetter
) {

    @PostConstruct
    fun validateUniqueDefinitions() {
        val renditionNameToNotUniqueDefinitionsMap = promenaRenditionDefinitionGetter.getAll()
            .groupBy { it.getRenditionName() }
            .filter { (_, definitions) -> definitions.size >= 2 }
            .toMap()

        check(renditionNameToNotUniqueDefinitionsMap.isEmpty()) {
            "Detected <${renditionNameToNotUniqueDefinitionsMap.size}> definitions with duplicated rendition name:\n" +
                    renditionNameToNotUniqueDefinitionsMap.entries.joinToString("\n") { (renditionName, definitions) ->
                        "> $renditionName: <${definitions.joinToString(", ") { it::class.java.canonicalName }}>"
                    }
        }
    }
}