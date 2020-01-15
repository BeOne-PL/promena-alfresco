package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.NodeRef

interface NodesChecksumGenerator {

    /**
     * Generates a checksum based on [nodeRefs].
     */
    fun generate(nodeRefs: List<NodeRef>): String
}