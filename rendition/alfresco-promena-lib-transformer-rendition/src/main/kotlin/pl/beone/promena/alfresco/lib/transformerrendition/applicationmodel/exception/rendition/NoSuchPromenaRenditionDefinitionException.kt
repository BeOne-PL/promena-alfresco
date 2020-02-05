package pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition

import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinition

/**
 * Signals that [renditionName] isn't available. [promenaRenditionDefinitions] contains available definitions.
 */
class NoSuchPromenaRenditionDefinitionException(
    val renditionName: String,
    val promenaRenditionDefinitions: List<PromenaRenditionDefinition>
) : NoSuchElementException(
    "There is no <$renditionName> Promena rendition definition. Available renditions: <[${createDescription(
        promenaRenditionDefinitions
    )}]>"
)

private fun createDescription(promenaRenditionDefinitions: List<PromenaRenditionDefinition>) =
    promenaRenditionDefinitions.joinToString(", ") { "${it::class.java.canonicalName}(${it.getRenditionName()})" }

