package pl.beone.promena.alfresco.module.core.contract.data

import pl.beone.promena.transformer.contract.model.data.Data

interface DataCleaner {

    /**
     * Cleans resources associated with [datas].
     */
    fun clean(datas: List<Data>)
}