package pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition

import org.alfresco.service.cmr.repository.ChildAssociationRef
import org.alfresco.service.cmr.repository.NodeRef

interface PromenaRenditionTransformationExecutor {

    /**
     * Constructs a transformation for [renditionName] using [nodeRef], executes it on Promena synchronously and wait for a result.
     *
     * @return a rendition node of [nodeRef]
     */
    fun transform(nodeRef: NodeRef, renditionName: String): ChildAssociationRef

    /**
     * Constructs a transformation for [renditionName] using [nodeRef] and executes it on Promena asynchronously.
     */
    fun transformAsync(nodeRef: NodeRef, renditionName: String)
}