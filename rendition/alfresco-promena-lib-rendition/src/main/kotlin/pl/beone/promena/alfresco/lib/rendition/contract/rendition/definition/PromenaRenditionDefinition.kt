package pl.beone.promena.alfresco.lib.rendition.contract.rendition.definition

import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.rendition.PromenaRenditionTransformationNotSupportedException
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * It is the equivalent of [ThumbnailDefinition][org.alfresco.repo.thumbnail.ThumbnailDefinition] in Promena environment.
 */
interface PromenaRenditionDefinition {

    /**
     * @see [ThumbnailDefinition.name][org.alfresco.repo.thumbnail.ThumbnailDefinition.name]
     */
    fun getRenditionName(): String

    /**
     * @see [ThumbnailDefinition.mimetype][org.alfresco.repo.thumbnail.ThumbnailDefinition.mimetype]
     */
    fun getTargetMediaType(): MediaType

    /**
     * @return a transformation that will be used to transform a data from [mediaType] to [getTargetMediaType]
     * @throws PromenaRenditionTransformationNotSupportedException if the [mediaType] isn't supported
     */
    fun getTransformation(mediaType: MediaType): Transformation

    /**
     * @see [ThumbnailDefinition.placeHolderResourcePath][org.alfresco.repo.thumbnail.ThumbnailDefinition.placeHolderResourcePath]
     */
    fun getPlaceHolderResourcePath(): String? = null

    /**
     * @see [ThumbnailDefinition.mimeAwarePlaceHolderResourcePath][org.alfresco.repo.thumbnail.ThumbnailDefinition.mimeAwarePlaceHolderResourcePath]
     */
    fun getMimeAwarePlaceHolderResourcePath(): String? = null
}