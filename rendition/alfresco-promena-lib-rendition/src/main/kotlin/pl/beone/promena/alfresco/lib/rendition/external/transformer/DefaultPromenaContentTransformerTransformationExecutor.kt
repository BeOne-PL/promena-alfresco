package pl.beone.promena.alfresco.lib.rendition.external.transformer

import mu.KotlinLogging
import org.alfresco.model.ContentModel
import org.alfresco.model.ContentModel.*
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator
import org.alfresco.repo.security.authentication.AuthenticationUtil
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.repository.ContentAccessor
import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.namespace.NamespaceService.CONTENT_MODEL_1_0_URI
import org.alfresco.service.namespace.QName
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.PromenaContentTransformerTransformationExecutor
import pl.beone.promena.alfresco.lib.rendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.alfresco.module.core.applicationmodel.node.singleNodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaType
import pl.beone.promena.transformer.applicationmodel.mediatype.mediaType
import java.util.*

class DefaultPromenaContentTransformerTransformationExecutor(
    private val serviceRegistry: ServiceRegistry,
    private val promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter,
    private val promenaTransformationExecutor: PromenaTransformationExecutor,
    private val promenaTransformationManager: PromenaTransformationManager,
    private val authorizationService: AuthorizationService
) : PromenaContentTransformerTransformationExecutor {

    companion object {
        private val TEMP_PREFIX = "Temp-"

        private val logger = KotlinLogging.logger {}
    }

    override fun execute(reader: ContentReader, writer: ContentWriter) {
        val mediaType = determineMediaType(reader)
        val targetMediaType = determineMediaType(writer)

        val transformation = promenaContentTransformerDefinitionGetter.getTransformation(mediaType, targetMediaType)

        val (temporaryFolderNode, nodeRef) = runInNewWritableTransactionAsAdmin {
            val temporaryFolderNode = createTempFolderNode()
            temporaryFolderNode to createNodeAndPutContent(temporaryFolderNode, reader)
        }

        try {
            val transformedNodeRef = runInNewWritableTransactionAsCurrentUser {
                promenaTransformationExecutor.execute(transformation, singleNodeDescriptor(nodeRef))
                    .let { transformationExecution -> getTransformedNodeRef(transformationExecution) }
            }

            serviceRegistry.contentService.getReader(transformedNodeRef, PROP_CONTENT)
                .also { transformedNodeRefContentReader -> writer.putContent(transformedNodeRefContentReader) }
        } finally {
            runInNewWritableTransactionAsAdmin {
                serviceRegistry.nodeService.deleteNode(temporaryFolderNode)
            }
        }
    }

    private fun determineMediaType(contentAccessor: ContentAccessor): MediaType =
        mediaType(contentAccessor.mimetype, contentAccessor.encoding)

    private fun createTempFolderNode(): NodeRef {
        val folderName = TEMP_PREFIX + UUID.randomUUID().toString()

        return serviceRegistry.nodeService.createNode(
            getCompanyHomeNodeRef(),
            ASSOC_CONTAINS,
            QName.createQName(CONTENT_MODEL_1_0_URI, folderName),
            TYPE_FOLDER,
            mapOf(ContentModel.PROP_NAME to folderName)
        ).childRef
    }

    private fun createNodeAndPutContent(folderNodeRef: NodeRef, contentReader: ContentReader): NodeRef =
        serviceRegistry.nodeService.createNode(
            folderNodeRef,
            ASSOC_CONTAINS,
            QName.createQName(CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString()),
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
        authorizationService.runAs(AuthenticationUtil.getAdminUserName()) {
            serviceRegistry.retryingTransactionHelper.doInTransaction(toRun, false, true)
        }

    private fun <T> runInNewWritableTransactionAsCurrentUser(toRun: () -> T): T =
        authorizationService.runAs(authorizationService.getCurrentUser()) {
            serviceRegistry.retryingTransactionHelper.doInTransaction(toRun, false, true)
        }

    private fun getCompanyHomeNodeRef(): NodeRef =
        serviceRegistry.nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null)
}