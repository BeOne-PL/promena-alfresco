package pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

interface PromenaContentTransformerDefinitionGetter {

    /**
     * @throws PromenaContentTransformerTransformationNotSupportedException if there is no transformation that is able to transform from [mediaType] to [targetMediaType]
     */
    fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation
}