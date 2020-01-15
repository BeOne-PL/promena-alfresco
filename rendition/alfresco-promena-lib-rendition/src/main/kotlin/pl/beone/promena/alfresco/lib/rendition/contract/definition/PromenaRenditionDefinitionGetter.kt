package pl.beone.promena.alfresco.lib.rendition.contract.definition

import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.NoSuchPromenaRenditionDefinitionException

interface PromenaRenditionDefinitionGetter {

    fun getAll(): List<PromenaRenditionDefinition>

    /**
     * @throws NoSuchPromenaRenditionDefinitionException if no [renditionName] is available
     */
    fun getByRenditionName(renditionName: String): PromenaRenditionDefinition
}