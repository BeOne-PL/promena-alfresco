package pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

interface PromenaContentTransformerDefinition {

    fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation

    fun getPriority(): Int
}