package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.InvalidNodeRefException
import org.alfresco.service.cmr.repository.NodeRef

interface NodesExistenceVerifier {

    /**
     * @throws InvalidNodeRefException if any of [nodeRefs] don't exist
     */
    fun verify(nodeRefs: List<NodeRef>)
}