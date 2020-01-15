package pl.beone.promena.alfresco.lib.rendition.contract

import org.alfresco.service.cmr.repository.ChildAssociationRef
import org.alfresco.service.cmr.repository.NodeRef

interface RenditionGetter {

    /**
     * @return all renditions nodes for [nodeRef]
     */
    fun getRenditions(nodeRef: NodeRef): List<ChildAssociationRef>

    /**
     * @return [renditionName] rendition node of [nodeRef] or `null` if it doesn't exist
     */
    fun getRendition(nodeRef: NodeRef, renditionName: String): ChildAssociationRef?
}