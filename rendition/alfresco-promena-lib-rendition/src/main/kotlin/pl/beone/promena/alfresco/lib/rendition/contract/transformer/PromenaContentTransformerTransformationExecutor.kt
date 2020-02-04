package pl.beone.promena.alfresco.lib.rendition.contract.transformer

import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter

interface PromenaContentTransformerTransformationExecutor {

    fun execute(reader: ContentReader, writer: ContentWriter)
}