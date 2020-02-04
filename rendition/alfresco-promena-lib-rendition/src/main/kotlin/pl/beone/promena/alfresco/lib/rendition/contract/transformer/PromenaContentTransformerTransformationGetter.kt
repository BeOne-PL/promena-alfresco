package pl.beone.promena.alfresco.lib.rendition.contract.transformer

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

interface PromenaContentTransformerTransformationGetter {

    fun get(mediaType: MediaType, targetMediaType: MediaType): Transformation
}