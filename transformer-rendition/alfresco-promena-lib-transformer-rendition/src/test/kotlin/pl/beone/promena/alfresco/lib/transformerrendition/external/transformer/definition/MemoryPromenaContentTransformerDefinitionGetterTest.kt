package pl.beone.promena.alfresco.lib.transformerrendition.external.transformer.definition

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import pl.beone.promena.alfresco.lib.transformerrendition.applicationmodel.exception.transformer.PromenaContentTransformerTransformationNotSupportedException
import pl.beone.promena.alfresco.lib.transformerrendition.contract.transformer.definition.PromenaContentTransformerDefinition
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.APPLICATION_MSWORD
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.APPLICATION_PDF
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.TEXT_PLAIN
import pl.beone.promena.transformer.applicationmodel.mediatype.MediaTypeConstants.TEXT_XML
import pl.beone.promena.transformer.contract.transformation.singleTransformation
import pl.beone.promena.transformer.internal.model.parameters.emptyParameters

class MemoryPromenaContentTransformerDefinitionGetterTest {

    companion object {
        private val toPlainTextTransformation = singleTransformation("converter", TEXT_PLAIN, emptyParameters())
        private val toPlainTextTransformation2 = singleTransformation("converter2", TEXT_PLAIN, emptyParameters())
        private val toApplicationPdfDefinitionTransformation = singleTransformation("converter", APPLICATION_PDF, emptyParameters())
    }

    private lateinit var promenaContentTransformerDefinitionGetter: MemoryPromenaContentTransformerDefinitionGetter

    private lateinit var fromApplicationPdfToPlainTextDefinition: PromenaContentTransformerDefinition
    private lateinit var fromApplicationPdfToPlainTextDefinition2: PromenaContentTransformerDefinition
    private lateinit var fromPlainTextToApplicationPdfDefinition: PromenaContentTransformerDefinition

    @Before
    fun setUp() {
        fromApplicationPdfToPlainTextDefinition = mockk {
            every { getTransformation(APPLICATION_PDF, TEXT_PLAIN) } returns toPlainTextTransformation
            every { getTransformation(not(APPLICATION_PDF), not(TEXT_PLAIN)) } throws
                    PromenaContentTransformerTransformationNotSupportedException.custom("no matter")

            every { getPriority() } returns 2
        }

        fromApplicationPdfToPlainTextDefinition2 = mockk {
            every { getTransformation(APPLICATION_PDF, TEXT_PLAIN) } returns toPlainTextTransformation2
            every { getTransformation(not(APPLICATION_PDF), not(TEXT_PLAIN)) } throws
                    PromenaContentTransformerTransformationNotSupportedException.custom("no matter")

            every { getPriority() } returns 1
        }

        fromPlainTextToApplicationPdfDefinition = mockk {
            every { getTransformation(TEXT_PLAIN, APPLICATION_PDF) } returns toApplicationPdfDefinitionTransformation
            every { getTransformation(not(TEXT_PLAIN), not(APPLICATION_PDF)) } throws
                    PromenaContentTransformerTransformationNotSupportedException.custom("no matter")

            every { getPriority() } returns 1
        }

        promenaContentTransformerDefinitionGetter = MemoryPromenaContentTransformerDefinitionGetter(
            listOf(fromApplicationPdfToPlainTextDefinition, fromApplicationPdfToPlainTextDefinition2, fromPlainTextToApplicationPdfDefinition)
        )
    }

    @Test
    fun getTransformation() {
        promenaContentTransformerDefinitionGetter.getTransformation(TEXT_PLAIN, APPLICATION_PDF) shouldBe toApplicationPdfDefinitionTransformation

    }

    @Test
    fun `getTransformation _ should respect priority`() {
        promenaContentTransformerDefinitionGetter.getTransformation(APPLICATION_PDF, TEXT_PLAIN) shouldBe toPlainTextTransformation2
    }

    @Test
    fun `getTransformation _ not supported transformation _ should throw PromenaContentTransformerTransformationNotSupportedException`() {
        shouldThrow<PromenaContentTransformerTransformationNotSupportedException> {
            promenaContentTransformerDefinitionGetter.getTransformation(TEXT_XML, APPLICATION_MSWORD)
        }
    }
}