package pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType

/**
 * Signals that a rendition transformation isn't possible for a [reason].
 */
class PromenaRenditionTransformationNotSupportedException internal constructor(
    reason: String
) : IllegalArgumentException(reason) {

    companion object {
        @JvmStatic
        fun unsupportedMediaType(
            renditionName: String,
            mediaType: MediaType,
            targetMediaType: MediaType
        ): PromenaRenditionTransformationNotSupportedException =
            PromenaRenditionTransformationNotSupportedException(
                "Rendition <$renditionName> transformation ${mediaType.createDescription()} -> ${targetMediaType.createDescription()} isn't supported"
            )

        @JvmStatic
        fun custom(reason: String): PromenaRenditionTransformationNotSupportedException =
            PromenaRenditionTransformationNotSupportedException(
                reason
            )

        private fun MediaType.createDescription(): String =
            "(${mimeType}, ${charset.name()})"
    }
}