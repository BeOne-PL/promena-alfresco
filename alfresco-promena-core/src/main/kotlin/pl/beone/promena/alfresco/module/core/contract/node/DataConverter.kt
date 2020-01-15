package pl.beone.promena.alfresco.module.core.contract.node

import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter
import pl.beone.promena.transformer.contract.model.data.Data

interface DataConverter {

    /**
     * @return [Data] with the content from [contentReader]
     */
    fun createData(contentReader: ContentReader): Data

    /**
     * Reads data from [data] and writes to [contentWriter].
     */
    fun saveDataInContentWriter(data: Data, contentWriter: ContentWriter)
}