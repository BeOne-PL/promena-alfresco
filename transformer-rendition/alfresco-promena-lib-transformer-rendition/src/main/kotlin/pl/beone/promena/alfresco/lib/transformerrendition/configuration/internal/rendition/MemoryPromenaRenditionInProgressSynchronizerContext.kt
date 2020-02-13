package pl.beone.promena.alfresco.lib.transformerrendition.configuration.internal.rendition

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.transformerrendition.internal.rendition.MemoryPromenaRenditionInProgressSynchronizer

@Configuration
class MemoryPromenaRenditionInProgressSynchronizerContext {

    @Bean
    fun memoryPromenaRenditionInProgressSynchronizer() =
        MemoryPromenaRenditionInProgressSynchronizer()
}