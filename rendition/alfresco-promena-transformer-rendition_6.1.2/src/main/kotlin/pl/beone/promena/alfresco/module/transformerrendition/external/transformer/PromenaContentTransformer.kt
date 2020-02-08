package pl.beone.promena.alfresco.module.transformerrendition.external.transformer

import org.alfresco.repo.content.transform.ContentTransformer
import org.alfresco.repo.content.transform.UnsupportedTransformationException
import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter
import org.alfresco.service.cmr.repository.TransformationOptions
import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.PromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.transformer.applicationmodel.mediatype.mediaType

internal class PromenaContentTransformer(
    private val promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter,
    private val promenaContentTransformerTransformationExecutor: PromenaContentTransformerTransformationExecutor
) : ContentTransformer {

    override fun transform(reader: ContentReader, writer: ContentWriter, options: MutableMap<String, Any>?) {
        transform(reader, writer)
    }

    override fun transform(reader: ContentReader, contentWriter: ContentWriter, options: TransformationOptions?) {
        transform(reader, contentWriter)
    }

    override fun transform(reader: ContentReader, writer: ContentWriter) {
        try {
            promenaContentTransformerTransformationExecutor.transform(reader, writer)
        } catch (e: PromenaContentTransformerTransformationNotSupportedException) {
            throw UnsupportedTransformationException(e.message)
        }
    }

    override fun isTransformableMimetype(sourceMimetype: String, targetMimetype: String, options: TransformationOptions?): Boolean =
        checkIfSupported(sourceMimetype, targetMimetype)

    override fun isTransformable(sourceMimetype: String, targetMimetype: String, options: TransformationOptions?): Boolean =
        checkIfSupported(sourceMimetype, targetMimetype)

    override fun isTransformable(sourceMimetype: String, sourceSize: Long, targetMimetype: String, options: TransformationOptions?): Boolean =
        checkIfSupported(sourceMimetype, targetMimetype)

    override fun getName(): String =
        "Promena"

    override fun getComments(available: Boolean): String =
        "Promena"

    override fun getMaxSourceSizeKBytes(sourceMimetype: String?, targetMimetype: String?, options: TransformationOptions?): Long =
        Long.MAX_VALUE

    override fun isTransformableSize(sourceMimetype: String?, sourceSize: Long, targetMimetype: String?, options: TransformationOptions?): Boolean =
        true

    override fun isExplicitTransformation(sourceMimetype: String?, targetMimetype: String?, options: TransformationOptions?): Boolean =
        true

    override fun getTransformationTime(): Long =
        -1

    override fun getTransformationTime(sourceMimetype: String?, targetMimetype: String?): Long =
        -1

    private fun checkIfSupported(sourceMimetype: String, targetMimetype: String): Boolean =
        try {
            promenaContentTransformerDefinitionGetter.getTransformation(mediaType(sourceMimetype), mediaType(targetMimetype))
            true
        } catch (e: PromenaContentTransformerTransformationNotSupportedException) {
            false
        }

}