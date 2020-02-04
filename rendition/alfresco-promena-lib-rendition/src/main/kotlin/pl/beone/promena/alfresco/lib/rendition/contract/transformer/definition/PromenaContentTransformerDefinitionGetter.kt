package pl.beone.promena.alfresco.lib.rendition.contract.transformer.definition

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

interface PromenaContentTransformerDefinitionGetter {

    fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation
}