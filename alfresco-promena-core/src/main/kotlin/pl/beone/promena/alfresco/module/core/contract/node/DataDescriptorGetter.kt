package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.InvalidNodeRefException
import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.transformer.contract.data.DataDescriptor

interface DataDescriptorGetter {

    /**
     * @return [DataDescriptor] created based on [nodeDescriptor]
     * @throws InvalidNodeRefException if any node doesn't exist
     */
    fun get(nodeDescriptor: NodeDescriptor): DataDescriptor
}