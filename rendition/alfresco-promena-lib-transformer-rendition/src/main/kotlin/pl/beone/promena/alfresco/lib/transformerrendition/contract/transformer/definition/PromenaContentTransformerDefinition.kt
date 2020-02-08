package pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * It is used to provide Content Transformers equivalent in Promena environment.
 */
interface PromenaContentTransformerDefinition {

    /**
     * @return a transformation that is able to transform from [mediaType] to [targetMediaType]
     */
    fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation

    /**
     * @return a priority - a lower value indicates a higher priority
     */
    fun getPriority(): Int
}