package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.module.core.applicationmodel.exception.PotentialConcurrentModificationException

interface NodeInCurrentTransactionVerifier {

    /**
     * @throws PotentialConcurrentModificationException if [nodeRef] has been already modified in the current transaction (from the invoker perspective)
     */
    fun verify(nodeRef: NodeRef)
}