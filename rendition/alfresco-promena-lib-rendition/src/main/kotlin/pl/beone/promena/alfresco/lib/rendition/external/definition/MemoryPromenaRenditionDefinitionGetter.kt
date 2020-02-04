package pl.beone.promena.alfresco.lib.rendition.external.definition

import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.NoSuchPromenaRenditionDefinitionException
import pl.beone.promena.alfresco.lib.rendition.contract.definition.PromenaRenditionDefinition
import pl.beone.promena.alfresco.lib.rendition.contract.definition.PromenaRenditionDefinitionGetter

/**
 * Manages [promenaRenditionDefinitions] in memory.
 */
class MemoryPromenaRenditionDefinitionGetter(
    private val promenaRenditionDefinitions: List<PromenaRenditionDefinition>
) : PromenaRenditionDefinitionGetter {

    private val renditionNameToDefinitionMap =
        promenaRenditionDefinitions.map { it.getRenditionName() to it }.toMap()

    override fun getAll(): List<PromenaRenditionDefinition> =
        promenaRenditionDefinitions

    override fun getByRenditionName(renditionName: String): PromenaRenditionDefinition =
        renditionNameToDefinitionMap[renditionName]
            ?: throw NoSuchPromenaRenditionDefinitionException(renditionName, promenaRenditionDefinitions)
}