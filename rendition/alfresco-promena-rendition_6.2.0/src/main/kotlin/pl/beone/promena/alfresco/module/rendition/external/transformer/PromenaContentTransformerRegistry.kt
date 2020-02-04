@file:Suppress("DEPRECATION")

package pl.beone.promena.alfresco.module.rendition.external.transformer

import org.alfresco.repo.content.transform.ContentTransformer
import org.alfresco.repo.content.transform.ContentTransformerRegistry
import org.alfresco.repo.content.transform.TransformerDebug
import org.alfresco.service.cmr.repository.TransformationOptions
import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.transformer.applicationmodel.mediatype.mediaType

class PromenaContentTransformerRegistry(
    private val promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter,
    promenaContentTransformerTransformationExecutor: PromenaContentTransformerTransformationExecutor
) : ContentTransformerRegistry(null) {

    private val transformer = PromenaContentTransformer(
        promenaContentTransformerDefinitionGetter,
        promenaContentTransformerTransformationExecutor
    )

    override fun getActiveTransformers(
        sourceMimetype: String,
        sourceSize: Long,
        targetMimetype: String,
        options: TransformationOptions?
    ): List<ContentTransformer> =
        getTransformer(sourceMimetype, targetMimetype)?.let { listOf(it) } ?: emptyList()

    override fun getTransformers(): List<ContentTransformer> =
        listOf(transformer)

    override fun getAllTransformers(): List<ContentTransformer> =
        listOf(transformer)

    override fun getTransformer(transformerName: String): ContentTransformer? =
        throw UnsupportedOperationException("This function isn't supported by Promena")

    override fun getTransformer(sourceMimetype: String, targetMimetype: String, options: TransformationOptions?): ContentTransformer? =
        getTransformer(sourceMimetype, targetMimetype)

    override fun getTransformer(
        sourceMimetype: String,
        sourceSize: Long,
        targetMimetype: String,
        options: TransformationOptions?
    ): ContentTransformer? =
        getTransformer(sourceMimetype, targetMimetype)

    private fun getTransformer(mimetype: String, targetMimetype: String): PromenaContentTransformer? =
        try {
            promenaContentTransformerDefinitionGetter.getTransformation(mediaType(mimetype), mediaType(targetMimetype))
            transformer
        } catch (e: PromenaContentTransformerTransformationNotSupportedException) {
            null
        }

    override fun setEnabled(enabled: Boolean) {
        // deliberately omitted
    }

    override fun addTransformer(transformer: ContentTransformer?) {
        // deliberately omitted
    }

    override fun setTransformerDebug(transformerDebug: TransformerDebug?) {
        // deliberately omitted
    }

    override fun removeTransformer(transformer: ContentTransformer?) {
        // deliberately omitted
    }

    override fun addComponentTransformer(transformer: ContentTransformer?) {
        // deliberately omitted
    }
}