package pl.beone.promena.alfresco.module.transformerrendition.external.rendition2

import org.alfresco.repo.rendition2.RenditionDefinitionRegistry2
import org.alfresco.repo.rendition2.RenditionService2
import org.alfresco.service.cmr.repository.ChildAssociationRef
import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.PromenaRenditionTransformationExecutor
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.RenditionGetter

class PromenaRenditionService2(
    private val renditionGetter: RenditionGetter,
    private val promenaRenditionTransformationExecutor: PromenaRenditionTransformationExecutor,
    private val renditionDefinitionRegistry2: PromenaRenditionDefinitionRegistry2
) : RenditionService2 {

    override fun isEnabled(): Boolean =
        true

    override fun getRenditionByName(sourceNodeRef: NodeRef, renditionName: String): ChildAssociationRef? =
        renditionGetter.getRendition(sourceNodeRef, renditionName)

    override fun render(sourceNodeRef: NodeRef, renditionName: String) {
        promenaRenditionTransformationExecutor.transformAsync(sourceNodeRef, renditionName)
    }

    override fun getRenditions(sourceNodeRef: NodeRef): List<ChildAssociationRef> =
        renditionGetter.getRenditions(sourceNodeRef)

    override fun getRenditionDefinitionRegistry2(): RenditionDefinitionRegistry2 =
        renditionDefinitionRegistry2
}