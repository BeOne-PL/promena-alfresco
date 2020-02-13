package pl.beone.promena.alfresco.lib.transformerrendition.internal.rendition

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.PromenaRenditionInProgressException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.PromenaRenditionInProgressSynchronizer
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution

/**
 * Persists all rendition transformation execution in memory.
 * A rendition transformation is associated with a specified Alfresco Content Services instance
 * so there is no need to recover the state after a reboot.
 */
class MemoryPromenaRenditionInProgressSynchronizer :
    PromenaRenditionInProgressSynchronizer {

    private data class RenditionKey(
        val nodeRef: NodeRef,
        val renditionName: String
    )

    private val renditions = HashMap<RenditionKey, TransformationExecution>()

    @Synchronized
    override fun start(nodeRef: NodeRef, renditionName: String, transformationExecution: TransformationExecution) {
        renditions[RenditionKey(nodeRef, renditionName)] = transformationExecution
    }

    @Synchronized
    override fun finish(nodeRef: NodeRef, renditionName: String) {
        renditions.remove(RenditionKey(nodeRef, renditionName)
        )
    }

    @Synchronized
    override fun isInProgress(nodeRef: NodeRef, renditionName: String) {
        val transformationExecution = renditions[RenditionKey(nodeRef, renditionName)]
        if (transformationExecution != null) {
            throw PromenaRenditionInProgressException(nodeRef, renditionName, transformationExecution)
        }
    }
}