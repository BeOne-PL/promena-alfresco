package pl.beone.promena.alfresco.module.core.applicationmodel.exception

import org.alfresco.service.cmr.repository.NodeRef

/**
 * Signals that [nodeRef] has been modified more than once in a given transaction.
 */
class PotentialConcurrentModificationException(
    val nodeRef: NodeRef
) : ConcurrentModificationException("Node <$nodeRef> has been modified in this transaction. It's highly probable that it may cause concurrency problems. Complete this transaction before executing transformation")