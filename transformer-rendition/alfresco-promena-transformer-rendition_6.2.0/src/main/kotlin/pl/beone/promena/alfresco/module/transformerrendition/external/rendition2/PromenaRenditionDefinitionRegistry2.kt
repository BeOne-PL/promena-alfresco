package pl.beone.promena.alfresco.module.transformerrendition.external.rendition2

import org.alfresco.repo.rendition2.RenditionDefinition2
import org.alfresco.repo.rendition2.RenditionDefinitionRegistry2
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinitionGetter

class PromenaRenditionDefinitionRegistry2(
    private val promenaRenditionDefinitionGetter: PromenaRenditionDefinitionGetter
) : RenditionDefinitionRegistry2 {

    override fun getRenditionNamesFrom(sourceMimetype: String?, size: Long): Set<String> =
        emptySet()

    override fun getRenditionNames(): Set<String> =
        promenaRenditionDefinitionGetter.getAll()
            .map(PromenaRenditionDefinition::getRenditionName)
            .toSet()

    override fun getRenditionDefinition(renditionName: String): RenditionDefinition2 =
        PromenaRenditionDefinition2(renditionName)
}