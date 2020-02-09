package pl.beone.promena.alfresco.lib.transformerrendition.external.transformer.definition

import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinition
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * Manages [promenaContentTransformerDefinitions] in memory.
 */
class MemoryPromenaContentTransformerDefinitionGetter(
    private val promenaContentTransformerDefinitions: List<PromenaContentTransformerDefinition>
) : PromenaContentTransformerDefinitionGetter {

    private val definitionsOrderedByPriority =
        promenaContentTransformerDefinitions.sortedBy { it.getPriority() }

    override fun getTransformation(mediaType: MediaType, targetMediaType: MediaType): Transformation =
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