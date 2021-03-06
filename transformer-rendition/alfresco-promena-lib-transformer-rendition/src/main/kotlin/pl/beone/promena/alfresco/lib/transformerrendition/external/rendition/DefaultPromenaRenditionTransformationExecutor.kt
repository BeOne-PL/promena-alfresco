package pl.beone.promena.alfresco.lib.transformerrendition.external.rendition

import mu.KotlinLogging
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.repository.ChildAssociationRef
import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.PromenaRenditionInProgressException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.PromenaRenditionInProgressSynchronizer
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.PromenaRenditionTransformationExecutor
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.RenditionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.definition.PromenaRenditionDefinitionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.extension.getMediaType
import pl.beone.promena.alfresco.lib.transformerrendition.external.rendition.transformation.RenditionPromenaTransformationMetadataSaver
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaModel.PROPERTY_RENDITION_NAME
import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.node.toSingleNodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.noRetry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecutionResult
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutor
import pl.beone.promena.transformer.contract.transformation.Transformation
import pl.beone.promena.transformer.internal.model.metadata.emptyMetadata
import pl.beone.promena.transformer.internal.model.metadata.plus
import java.time.Duration

/**
 * Uses [PromenaTransformationManager] to delegate an execution and uses [PromenaTransformationManager] to get a result.
 * It is done in the same like an ordinary transformation. The only difference is that `renditionName` is added to every metadata of nodes
 * to be saved [RenditionPromenaTransformationMetadataSaver] as a property after a transformation execution.
 * Retrying mechanism is disabled.
 */
class DefaultPromenaRenditionTransformationExecutor(
    private val serviceRegistry: ServiceRegistry,
    private val renditionGetter: RenditionGetter,
    private val promenaRenditionDefinitionGetter: PromenaRenditionDefinitionGetter,
    private val promenaRenditionInProgressSynchronizer: PromenaRenditionInProgressSynchronizer,
    private val promenaTransformationExecutor: PromenaTransformationExecutor,
    private val promenaTransformationManager: PromenaTransformationManager,
    private val timeout: Duration
) : PromenaRenditionTransformationExecutor {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun transform(nodeRef: NodeRef, renditionName: String): ChildAssociationRef =
        try {
            transform(nodeRef, renditionName) { transformationExecution ->
                waitForResultAndGetRendition(nodeRef, renditionName, transformationExecution)
            }
        } catch (e: PromenaRenditionInProgressException) {
            logger.debug { "Transforming <$renditionName> rendition of <$nodeRef> is in progress in transaction <${e.transformationExecution.id}>. Waiting for result..." }

            waitForResultAndGetRendition(nodeRef, renditionName, e.transformationExecution)
        }

    private fun waitForResultAndGetRendition(
        nodeRef: NodeRef,
        renditionName: String,
        transformationExecution: TransformationExecution
    ): ChildAssociationRef {
        promenaTransformationManager.getResult(transformationExecution, timeout)

        return renditionGetter.getRendition(nodeRef, renditionName)
            ?: throw NoSuchElementException("There is no <$renditionName> rendition of <$nodeRef>")
    }

    override fun transformAsync(nodeRef: NodeRef, renditionName: String) {
        try {
            transform(nodeRef, renditionName) {}
        } catch (e: PromenaRenditionInProgressException) {
            logger.debug { "Skipped. Transforming <$renditionName> rendition of <$nodeRef> is in progress in transaction <${e.transformationExecution.id}>..." }
        }
    }

    @Synchronized
    private fun <T> transform(nodeRef: NodeRef, renditionName: String, toRun: (TransformationExecution) -> T): T {
        promenaRenditionInProgressSynchronizer.isInProgress(nodeRef, renditionName)
        val transformation = getTransformation(nodeRef, renditionName)
        return try {
            val transformationExecution = promenaTransformationExecutor.execute(
                transformation,
                creatRenditionNodeDescriptor(nodeRef, renditionName),
                FinishPostTransformationExecutor(promenaRenditionInProgressSynchronizer, renditionName),
                noRetry()
            )

            promenaRenditionInProgressSynchronizer.start(nodeRef, renditionName, transformationExecution)

            logger.debug { "Transforming <$renditionName> rendition of <$nodeRef>..." }

            toRun(transformationExecution)
        } catch (e: Exception) {
            promenaRenditionInProgressSynchronizer.finish(nodeRef, renditionName)

            logger.error(e) { "Couldn't transform <$renditionName> rendition of <$nodeRef>" }
            throw e
        }
    }

    private fun getTransformation(nodeRef: NodeRef, renditionName: String): Transformation =
        promenaRenditionDefinitionGetter
            .getByRenditionName(renditionName)
            .getTransformation(serviceRegistry.contentService.getMediaType(nodeRef))

    private fun creatRenditionNodeDescriptor(nodeRef: NodeRef, renditionName: String): NodeDescriptor =
        nodeRef.toSingleNodeDescriptor(emptyMetadata() + (PROPERTY_RENDITION_NAME.localName to renditionName))

    internal class FinishPostTransformationExecutor(
        private val promenaRenditionInProgressSynchronizer: PromenaRenditionInProgressSynchronizer,
        private val renditionName: String
    ) : PostTransformationExecutor() {

        companion object {
            private val logger = KotlinLogging.logger {}
        }

        override fun execute(transformation: Transformation, nodeDescriptor: NodeDescriptor, result: TransformationExecutionResult) {
            val nodeRef = nodeDescriptor.descriptors[0].nodeRef

            promenaRenditionInProgressSynchronizer.finish(nodeRef, renditionName)

            logger.debug { "Transformed <$renditionName> rendition of <$nodeRef>" }
        }
    }
}
