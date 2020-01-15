package pl.beone.promena.alfresco.module.core.applicationmodel.exception

import org.alfresco.service.cmr.repository.NodeRef

/**
 * Signals that a checksum of [nodeRefs] has changed from [oldNodesChecksum] to [currentNodesChecksum].
 */
class NodesInconsistencyException(
    val nodeRefs: List<NodeRef>,
    val oldNodesChecksum: String,
    val currentNodesChecksum: String
) : IllegalStateException("Nodes <$nodeRefs> have changed in the meantime (old checksum <$oldNodesChecksum>, current checksum <$currentNodesChecksum>)")