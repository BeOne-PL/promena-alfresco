package pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer

import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType

class PromenaContentTransformerTransformationNotSupportedException internal constructor(
    reason: String
) : IllegalArgumentException(reason) {

    companion object {
        @JvmStatic
        fun unsupportedMediaType(mediaType: MediaType, targetMediaType: MediaType): PromenaContentTransformerTransformationNotSupportedException =
            PromenaContentTransformerTransformationNotSupportedException("Content transformer transformation ${mediaType.createDescription()} -> ${targetMediaType.createDescription()} isn't supported")

        @JvmStatic
        fun custom(reason: String): PromenaContentTransformerTransformationNotSupportedException =
            PromenaContentTransformerTransformationNotSupportedException(reason)

        private fun MediaType.createDescription(): String =
            "(${mimeType}, ${charset.name()})"
    }
}