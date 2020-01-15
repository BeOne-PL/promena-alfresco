package pl.beone.promena.alfresco.module.core.contract.transformation

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.transformer.contract.data.TransformedDataDescriptor
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * A transformer can provide custom metadata
 * and should implement this interface to transform a metadata into Alfresco Content Services persistence model.
 */
interface PromenaTransformationMetadataSaver {

    /**
     * Saves the metadata of [transformedDataDescriptor] in [transformedNodeRefs].
     *
     * @param nodeRefs the nodes participated in the transformation
     * @param transformedDataDescriptor the result of the transformation
     * @param transformedNodeRefs the nodes created from the transformation
     */
    fun save(
        nodeRefs: List<NodeRef>,
        transformation: Transformation,
        transformedDataDescriptor: TransformedDataDescriptor,
        transformedNodeRefs: List<NodeRef>
    )
}