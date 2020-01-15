package pl.beone.promena.alfresco.module.core.applicationmodel.node

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.transformer.contract.model.Metadata
import pl.beone.promena.transformer.internal.model.metadata.emptyMetadata

/**
 * Provides a full description of nodes used by transformers to perform a transformation.
 * A node descriptor can consist of many [NodeDescriptor.Single]. Many [NodeDescriptor.Single] make up [NodeDescriptor.Multi].
 *
 * @see NodeDescriptorDsl
 */
sealed class NodeDescriptor {

    /**
     * @param metadata the metadata of the node
     */
    data class Single internal constructor(
        val nodeRef: NodeRef,
        val metadata: Metadata
    ) : NodeDescriptor() {

        companion object {
            @JvmStatic
            fun of(nodeRef: NodeRef, metadata: Metadata): Single =
                Single(nodeRef, metadata)

            @JvmStatic
            fun of(nodeRef: NodeRef): Single =
                Single(nodeRef, emptyMetadata())
        }

        override val descriptors: List<Single>
            get() = listOf(this)
    }

    data class Multi internal constructor(
        override val descriptors: List<Single>
    ) : NodeDescriptor() {

        companion object {
            @JvmStatic
            fun of(descriptors: List<Single>): Multi =
                Multi(descriptors)
        }

    }

    /**
     * The list of [NodeDescriptor.Single] making up the whole node descriptor.
     */
    abstract val descriptors: List<Single>
}