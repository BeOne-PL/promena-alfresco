package pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.NoSuchPromenaRenditionDefinitionException

interface PromenaRenditionDefinitionGetter {

    fun getAll(): List<PromenaRenditionDefinition>

    /**
     * @throws NoSuchPromenaRenditionDefinitionException if no [renditionName] is available
     */
    fun getByRenditionName(renditionName: String): PromenaRenditionDefinition
}