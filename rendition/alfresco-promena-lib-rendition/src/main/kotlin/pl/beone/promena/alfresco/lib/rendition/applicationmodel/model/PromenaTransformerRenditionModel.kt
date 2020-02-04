package pl.beone.promena.alfresco.module.core.applicationmodel.model

import org.alfresco.service.namespace.QName.createQName
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaTransformerRenditionNamespace.PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI

object PromenaTransformerRenditionModel {

    @JvmField
    val TYPE_TRANSFORMATIONS = createQName(PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI, "transformations")!!

    @JvmField
    val ASSOCIATION_CONTAINS = createQName(PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI, "contains")!!
}