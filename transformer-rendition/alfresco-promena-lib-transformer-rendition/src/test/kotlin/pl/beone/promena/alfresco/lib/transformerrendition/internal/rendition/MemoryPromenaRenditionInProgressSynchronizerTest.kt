package pl.beone.promena.alfresco.lib.transformerrendition.internal.rendition

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import org.alfresco.service.cmr.repository.NodeRef
import org.junit.Before
import org.junit.Test
import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.rendition.PromenaRenditionInProgressException
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.transformationExecution

class MemoryPromenaRenditionInProgressSynchronizerTest {

    private lateinit var memoryPromenaRenditionInProgressManager: MemoryPromenaRenditionInProgressSynchronizer

    @Before
    fun setUp() {
        memoryPromenaRenditionInProgressManager =
            MemoryPromenaRenditionInProgressSynchronizer()
    }

    @Test
    fun all() {
        val transformationExecution = transformationExecution("1")

        val nodeRef = NodeRef("workspace://SpacesStore/7abdf1e2-92f4-47b2-983a-611e42f3555c")
        val renditionName = "doclib"

        val nodeRef2 = NodeRef("workspace:/workspace://SpacesStore/b0bfb14c-be38-48be-90c3-cae4a7fd0c8f")
        val renditionName2 = "pdf"

        memoryPromenaRenditionInProgressManager.start(nodeRef, renditionName, transformationExecution)

        shouldNotThrow<PromenaRenditionInProgressException> {
            memoryPromenaRenditionInProgressManager.isInProgress(nodeRef2, renditionName2)
        }

        memoryPromenaRenditionInProgressManager.start(nodeRef2, renditionName2, transformationExecution)
        shouldThrow<PromenaRenditionInProgressException> {
            memoryPromenaRenditionInProgressManager.isInProgress(nodeRef2, renditionName2)
        }.message shouldBe "Creating rendition <pdf> of <workspace:/workspace://SpacesStore/b0bfb14c-be38-48be-90c3-cae4a7fd0c8f> is in progress in transformation <1>..."

        memoryPromenaRenditionInProgressManager.finish(nodeRef2, renditionName2)
        shouldNotThrow<PromenaRenditionInProgressException> {
            memoryPromenaRenditionInProgressManager.isInProgress(nodeRef2, renditionName2)
        }
    }
}