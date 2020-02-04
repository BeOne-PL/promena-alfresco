package pl.beone.promena.alfresco.lib.rendition.external.transformer

import mu.KotlinLogging
import org.alfresco.model.ContentModel.*
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator
import org.alfresco.repo.security.authentication.AuthenticationUtil
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.repository.ContentAccessor
import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.namespace.QName
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaTransformerRenditionModel.ASSOCIATION_CONTAINS
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaTransformerRenditionModel.TYPE_TRANSFORMATIONS
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaTransformerRenditionNamespace.PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI
import pl.beone.promena.alfresco.module.core.applicationmodel.node.singleNodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.applicationmodel.mediatype.mediaType
import java.util.*

class DefaultPromenaContentTransformerTransformationExecutor(
    private val serviceRegistry: ServiceRegistry,
    private val promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter,
    private val promenaTransformationExecutor: PromenaTransformationExecutor,
    private val promenaTransformationManager: PromenaTransformationManager
) : PromenaContentTransformerTransformationExecutor {

    companion object {
        private const val PROMENA_TRANSFORMER_RENDITION_TRANSFORMATIONS_NAME = "Transformations"

        private val logger = KotlinLogging.logger {}
    }

    private val promenaTransformerRenditionTransformationsNode: NodeRef by lazy {
        runInNewWritableTransactionAsAdmin {
            getTransformerRenditionTransformationsNode() ?: createTransformerRenditionTransformationsNode()
        }
    }

    override fun execute(reader: ContentReader, writer: ContentWriter) {
        val mediaType = determineMediaType(reader)
        val targetMediaType = determineMediaType(writer)

        val transformation = promenaContentTransformerDefinitionGetter.getTransformation(mediaType, targetMediaType)

        val nodeRef = runInNewWritableTransactionAsAdmin {
            createNodeAndPutContent(promenaTransformerRenditionTransformationsNode, reader)
        }

        try {
            val transformedNodeRef = runInNewWritableTransactionAsAdmin {
                promenaTransformationExecutor.execute(transformation, singleNodeDescriptor(nodeRef))
                    .let { transformationExecution -> getTransformedNodeRef(transformationExecution) }
            }

            serviceRegistry.contentService.getReader(transformedNodeRef, PROP_CONTENT)
                .also { transformedNodeRefContentReader -> writer.putContent(transformedNodeRefContentReader) }
        } finally {
            runInNewWritableTransactionAsAdmin {
                serviceRegistry.nodeService.deleteNode(nodeRef)
            }
        }
    }

    private fun determineMediaType(contentAccessor: ContentAccessor): MediaType =
        mediaType(contentAccessor.mimetype, contentAccessor.encoding)

    private fun createNodeAndPutContent(folderNodeRef: NodeRef, contentReader: ContentReader): NodeRef =
        serviceRegistry.nodeService.createNode(
            folderNodeRef,
            ASSOCIATION_CONTAINS,
            QName.createQName(PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI, UUID.randomUUID().toString()),
            TYPE_CONTENT
        ).apply {
            serviceRegistry.nodeService.addAspect(childRef, ASPECT_TEMPORARY, mutableMapOf())
            serviceRegistry.contentService.getWriter(childRef, PROP_CONTENT, true).apply {
                mimetype = contentReader.mimetype
                encoding = contentReader.encoding
            }.putContent(contentReader)
        }.childRef

    private fun getTransformedNodeRef(transformationExecution: TransformationExecution): NodeRef {
        val transformedNodeRefs = promenaTransformationManager.getResult(transformationExecution, null).nodeRefs

        check(transformedNodeRefs.isNotEmpty()) { "Transformation execution returned <0> elements but should at least <1>" }
        if (transformedNodeRefs.size >= 2) {
            logger.warn { "Transformation execution returned <${transformedNodeRefs.size}> elements. The first one was taken" }
        }

        return transformedNodeRefs[0]
    }

    private fun <T> runInNewWritableTransactionAsAdmin(toRun: () -> T): T =
        AuthenticationUtil.runAs(
            { serviceRegistry.retryingTransactionHelper.doInTransaction(toRun, false, true) },
            AuthenticationUtil.getAdminUserName()
        )

    private fun getTransformerRenditionTransformationsNode(): NodeRef? =
        serviceRegistry.nodeService.getChildByName(
            getCompanyHomeNodeRef(),
            ASSOC_CONTAINS,
            PROMENA_TRANSFORMER_RENDITION_TRANSFORMATIONS_NAME
        )

    private fun createTransformerRenditionTransformationsNode(): NodeRef =
        serviceRegistry.nodeService.createNode(
            getCompanyHomeNodeRef(),
            ASSOC_CONTAINS,
            QName.createQName(PROMENA_TRANSFORMER_RENDITION_MODEL_1_0_URI, PROMENA_TRANSFORMER_RENDITION_TRANSFORMATIONS_NAME),
            TYPE_TRANSFORMATIONS,
            mapOf(
                PROP_NAME to PROMENA_TRANSFORMER_RENDITION_TRANSFORMATIONS_NAME,
                PROP_IS_INDEXED to false,
                PROP_IS_CONTENT_INDEXED to false
            )
        ).childRef

    private fun getCompanyHomeNodeRef(): NodeRef =
        serviceRegistry.nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null)
}