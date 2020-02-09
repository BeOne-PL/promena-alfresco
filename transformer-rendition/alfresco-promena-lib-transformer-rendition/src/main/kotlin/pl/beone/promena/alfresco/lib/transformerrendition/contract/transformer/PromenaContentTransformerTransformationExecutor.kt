package pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer

import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter

interface PromenaContentTransformerTransformationExecutor {

    /**
     * Constructs a transformation from [reader], executes it on Promena synchronously, wait for a result and save it in [writer].
     */
    fun transform(reader: ContentReader, writer: ContentWriter)
}