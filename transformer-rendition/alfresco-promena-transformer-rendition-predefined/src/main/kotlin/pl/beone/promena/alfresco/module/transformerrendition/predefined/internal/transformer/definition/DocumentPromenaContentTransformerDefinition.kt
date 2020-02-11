package pl.beone.promena.alfresco.module.transformerrendition.predefined.internal.transformer.definition

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinition
import pl.beone.promena.transformer.applicationmodel.exception.transformer.TransformationNotSupportedException
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.APPLICATION_PDF
import pl.beone.promena.transformer.contract.transformation.Transformation
import pl.beone.promena.transformer.contract.transformation.next
import pl.beone.promena.transformer.converter.libreoffice.applicationmodel.LibreOfficeConverterSupport
import pl.beone.promena.transformer.converter.libreoffice.applicationmodel.libreOfficeConverterTransformation
import pl.beone.promena.transformer.converter.pdfbox.applicationmodel.PdfBoxConverterSupport
import pl.beone.promena.transformer.converter.pdfbox.applicationmodel.pdfBoxConverterTransformation
import pl.beone.promena.transformer.internal.model.parameters.emptyParameters

/**
 * Provides the equivalent to Alfresco Content Transformer that converts between various document formats using
 * `converter - LibreOffice` and `converter - PDFBox` transformers.
 */
object DocumentPromenaContentTransformerDefinition : PromenaContentTransformerDefinition {

    override fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation =
        try {
            determineTransformation(mediaType, targetMediaType)
        } catch (e: TransformationNotSupportedException) {
            throw PromenaContentTransformerTransformationNotSupportedException.unsupportedMediaType(mediaType, targetMediaType)
        }

    private fun determineTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation =
        try {
            LibreOfficeConverterSupport.MediaTypeSupport.isSupported(mediaType, targetMediaType)

            libreOfficeConverterTransformation(targetMediaType, emptyParameters())
        } catch (e: TransformationNotSupportedException) {
            LibreOfficeConverterSupport.MediaTypeSupport.isSupported(mediaType, APPLICATION_PDF)
            PdfBoxConverterSupport.MediaTypeSupport.isSupported(APPLICATION_PDF, targetMediaType)

            libreOfficeConverterTransformation(APPLICATION_PDF, emptyParameters()) next
                    pdfBoxConverterTransformation(targetMediaType, emptyParameters())
        }

    override fun getPriority(): Int =
        1
}