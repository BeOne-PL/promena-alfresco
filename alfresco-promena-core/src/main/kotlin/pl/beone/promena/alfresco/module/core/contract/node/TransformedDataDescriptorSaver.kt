package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.transformer.contract.data.TransformedDataDescriptor
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * A implementation of this interface should transform results of Promena transformation into Alfresco Content Services persistence model.
 */
interface TransformedDataDescriptorSaver {

    /**
     * Saves the result of a transformation.
     *
     * @param executionId the execution of the transformation
     * @param nodeRefs the nodes participated in the transformation
     * @param transformedDataDescriptor the result of the transformation
     * @return created nodes
     */
    fun save(
        executionId: String,
        transformation: Transformation,
        nodeRefs: List<NodeRef>,
        transformedDataDescriptor: TransformedDataDescriptor
    ): List<NodeRef>
}