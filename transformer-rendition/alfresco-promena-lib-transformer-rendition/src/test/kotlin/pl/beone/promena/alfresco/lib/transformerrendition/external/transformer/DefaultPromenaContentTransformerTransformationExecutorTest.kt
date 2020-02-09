package pl.beone.promena.alfresco.lib.transformerrendition.external.transformer

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.alfresco.model.ContentModel.PROP_CONTENT
import org.alfresco.model.ContentModel.TYPE_CONTENT
import org.alfresco.rad.test.AbstractAlfrescoIT
import org.alfresco.rad.test.AlfrescoTestRunner
import org.alfresco.repo.content.filestore.FileContentWriter
import org.alfresco.service.cmr.repository.ContentReader
import org.alfresco.service.cmr.repository.ContentWriter
import org.alfresco.service.cmr.repository.NodeRef
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinitionGetter
import pl.beone.promena.alfresco.lib.transformerrendition.external.util.createNameBasedOnDate
import pl.beone.promena.alfresco.lib.transformerrendition.external.util.createOrGetIntegrationTestsFolder
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.noRetry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecution
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecutionResult
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationManager
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.APPLICATION_PDF
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.TEXT_PLAIN
import pl.beone.promena.transformer.contract.transformation.singleTransformation
import pl.beone.promena.transformer.internal.model.parameters.emptyParameters
import java.time.Duration

@RunWith(AlfrescoTestRunner::class)
class DefaultPromenaContentTransformerTransformationExecutorTest : AbstractAlfrescoIT() {

    companion object {
        private val mediaType = APPLICATION_PDF
        private val targetMediaType = TEXT_PLAIN

        private val transformation = singleTransformation("converter", targetMediaType, emptyParameters())
        private val transformationExecution = transformationExecution("1")

        private val waitMax = Duration.ofMinutes(10)
    }

    private lateinit var promenaContentTransformerTransformationExecutor: DefaultPromenaContentTransformerTransformationExecutor

    private lateinit var contentReader: ContentReader
    private lateinit var contentWriter: ContentWriter
    private lateinit var promenaContentTransformerDefinitionGetter: PromenaContentTransformerDefinitionGetter
    private lateinit var promenaTransformationExecutor: PromenaTransformationExecutor
    private lateinit var transformedNodeRef: NodeRef
    private lateinit var promenaTransformationManager: PromenaTransformationManager

    @Before
    fun setUp() {
        contentReader = mockk {
            every { mimetype } returns mediaType.mimeType
            every { encoding } returns mediaType.charset.name()

            every { contentInputStream } returns "input".byteInputStream()
        }

        promenaContentTransformerDefinitionGetter = mockk {
            every { getTransformation(mediaType, targetMediaType) } returns transformation
        }

        promenaTransformationExecutor = mockk {
            every { execute(transformation, any(), null, noRetry()) } returns transformationExecution
        }

        transformedNodeRef = serviceRegistry.createOrGetIntegrationTestsFolder().createResultNode()

        contentWriter = spyk(FileContentWriter(createTempFile())) {
            every { mimetype } returns targetMediaType.mimeType
            every { encoding } returns targetMediaType.charset.name()
        }

        promenaTransformationManager = mockk {
            every { getResult(transformationExecution, waitMax) } returns transformationExecutionResult(transformedNodeRef)
        }

        promenaContentTransformerTransformationExecutor = DefaultPromenaContentTransformerTransformationExecutor(
            serviceRegistry,
            promenaContentTransformerDefinitionGetter,
            promenaTransformationExecutor,
            promenaTransformationManager,
            waitMax
        )
    }

    @After
    fun tearDown() {
        if (::transformedNodeRef.isInitialized) {
            serviceRegistry.nodeService.deleteNode(transformedNodeRef)
        }
    }

    @Test
    fun transform() {
        promenaContentTransformerTransformationExecutor.transform(contentReader, contentWriter)

        contentWriter.reader.contentString shouldBe "output"
    }

    private fun NodeRef.createResultNode(): NodeRef =
        serviceRegistry.fileFolderService.create(this, createNameBasedOnDate(), TYPE_CONTENT).apply {
            serviceRegistry.contentService.getWriter(nodeRef, PROP_CONTENT, true).apply {
                mimetype = TEXT_PLAIN.mimeType
                encoding = TEXT_PLAIN.charset.name()
            }.putContent("output")
        }.nodeRef
}