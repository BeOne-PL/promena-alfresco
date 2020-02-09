package pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.definition

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.NoSuchPromenaRenditionDefinitionException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinitionGetter

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
        renditionNameToDefinitionMap[renditionName] ?: throw NoSuchPromenaRenditionDefinitionException(renditionName, promenaRenditionDefinitions)
}