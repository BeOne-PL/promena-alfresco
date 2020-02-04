package pl.beone.promena.alfresco.lib.rendition.external.transformer

import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerDefinition
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerTransformationGetter
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

class MemoryPromenaContentTransformerTransformationGetter(
    private val promenaContentTransformerDefinitions: List<PromenaContentTransformerDefinition>
) : PromenaContentTransformerTransformationGetter {

    private val definitionsOrderedByPriority =
        promenaContentTransformerDefinitions.sortedBy { it.getPriority() }

    override fun get(mediaType: MediaType, targetMediaType: MediaType): Transformation =
        definitionsOrderedByPriority.firstOrNull { checkIfSupported(it, mediaType, targetMediaType) }
            ?.getTransformation(mediaType, targetMediaType)
            ?: throw PromenaContentTransformerTransformationNotSupportedException.unsupportedMediaType(mediaType, targetMediaType)

    private fun checkIfSupported(definition: PromenaContentTransformerDefinition, mediaType: MediaType, targetMediaType: MediaType): Boolean =
        try {
            definition.getTransformation(mediaType, targetMediaType)
            true
        } catch (e: PromenaContentTransformerTransformationNotSupportedException) {
            false
        }
}