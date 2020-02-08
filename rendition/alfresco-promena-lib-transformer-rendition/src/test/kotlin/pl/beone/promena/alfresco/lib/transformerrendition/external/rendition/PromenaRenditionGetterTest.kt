package pl.beone.promena.alfresco.lib.transformerrendition.external.rendition

import io.kotlintest.shouldBe
import org.alfresco.model.ContentModel
import org.alfresco.model.RenditionModel
import org.alfresco.rad.test.AbstractAlfrescoIT
import org.alfresco.rad.test.AlfrescoTestRunner
import org.alfresco.service.cmr.repository.ChildAssociationRef
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.namespace.NamespaceService
import org.alfresco.service.namespace.QName
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.beone.promena.alfresco.lib.transformerrendition.contract.rendition.RenditionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.external.util.createNameBasedOnDate
import pl.beone.promena.alfresco.lib.transformerrendition.external.util.createOrGetIntegrationTestsFolder
import pl.beone.promena.alfresco.module.core.applicationmodel.model.PromenaModel

@RunWith(AlfrescoTestRunner::class)
class PromenaRenditionGetterTest : AbstractAlfrescoIT() {

    private lateinit var nodeRef: NodeRef
    private lateinit var nodeRefDoclib: ChildAssociationRef
    private lateinit var nodeRefDoclib2: ChildAssociationRef
    private lateinit var nodeRefPdf: ChildAssociationRef

    private lateinit var nodeRef2: NodeRef
    private lateinit var nodeRef2Pdf: ChildAssociationRef

    private lateinit var renditionGetter: RenditionGetter

    @Before
    fun setUp() {
        val integrationTestsFolder = serviceRegistry.createOrGetIntegrationTestsFolder()

        nodeRef = integrationTestsFolder.createNode()
        nodeRefDoclib = nodeRef.createRenditionNode().setRenditionName("doclib")
        nodeRefDoclib2 = nodeRef.createRenditionNode().setRenditionName("doclib")
        nodeRef.createRenditionNode()
        nodeRefPdf = nodeRef.createRenditionNode().setRenditionName("pdf")

        nodeRef2 = integrationTestsFolder.createNode()
        nodeRef2Pdf = nodeRef2.createRenditionNode().setRenditionName("pdf")

        renditionGetter =
            PromenaRenditionGetter(serviceRegistry.nodeService)
    }

    @Test
    fun getRenditions() {
        renditionGetter.getRenditions(nodeRef) shouldBe listOf(nodeRefDoclib, nodeRefDoclib2, nodeRefPdf)

        renditionGetter.getRenditions(nodeRef2) shouldBe listOf(nodeRef2Pdf)
    }

    @Test
    fun getRendition() {
        renditionGetter.getRendition(nodeRef, "doclib") shouldBe nodeRefDoclib2
        renditionGetter.getRendition(nodeRef, "pdf") shouldBe nodeRefPdf

        renditionGetter.getRendition(nodeRef2, "pdf") shouldBe nodeRef2Pdf
    }

    @Test
    fun getRendition_absentRenditions() {
        renditionGetter.getRendition(nodeRef, "absent") shouldBe null
    }

    private fun NodeRef.createNode(targetType: QName = ContentModel.TYPE_CONTENT, name: String? = null): NodeRef {
        val determinedNamePattern = name ?: createNameBasedOnDate()

        return serviceRegistry.fileFolderService.create(this, determinedNamePattern, targetType).nodeRef
    }

    private fun NodeRef.createRenditionNode(): ChildAssociationRef =
        serviceRegistry.nodeService.createNode(
            this,
            RenditionModel.ASSOC_RENDITION,
            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, createNameBasedOnDate()),
            ContentModel.TYPE_THUMBNAIL,
            emptyMap()
        )

    private fun ChildAssociationRef.setRenditionName(renditionName: String): ChildAssociationRef =
        this.also { serviceRegistry.nodeService.setProperty(this.childRef, PromenaModel.PROPERTY_RENDITION_NAME, renditionName) }
}