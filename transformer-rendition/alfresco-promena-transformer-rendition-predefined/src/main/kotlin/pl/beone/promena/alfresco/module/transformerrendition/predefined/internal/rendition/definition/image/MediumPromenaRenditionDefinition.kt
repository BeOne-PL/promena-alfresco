package pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.rendition.definition.image

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.PromenaRenditionTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition
import pl.beone.promena.transformer.applicationmodel.exception.transformer.TransformationNotSupportedException
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.IMAGE_JPEG
import pl.beone.promena.transformer.contract.transformation.Transformation
import pl.beone.promena.transformer.converter.imagemagick.applicationmodel.imageMagickConverterParameters
import pl.beone.promena.transformer.converter.imagemagick.applicationmodel.imageMagickConverterTransformation

/**
 * Provides the equivalent to `medium` Alfresco Rendition.
 */
object MediumPromenaRenditionDefinition : PromenaRenditionDefinition {

    override fun getRenditionName(): String =
        "medium"

    override fun getTargetMediaType(): MediaType =
        IMAGE_JPEG

    override fun getTransformation(mediaType: MediaType): Transformation =
        try {
            determineTransformation(
                mediaType,
                getTargetMediaType(),
                imageMagickConverterTransformation(
                    getTargetMediaType(),
                    imageMagickConverterParameters(width = 100, height = 100, ignoreAspectRatio = false, allowEnlargement = false)
                )
            )
        } catch (e: TransformationNotSupportedException) {
            throw PromenaRenditionTransformationNotSupportedException.unsupportedMediaType(getRenditionName(), mediaType, getTargetMediaType())
        }

    override fun getPlaceHolderResourcePath(): String? =
        "alfresco/thumbnail/thumbnail_placeholder_medium.jpg"
}