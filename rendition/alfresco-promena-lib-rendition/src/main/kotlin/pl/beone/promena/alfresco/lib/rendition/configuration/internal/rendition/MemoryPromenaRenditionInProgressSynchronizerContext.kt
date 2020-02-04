package pl.beone.promena.alfresco.lib.rendition.configuration.internal.rendition

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.beone.promena.alfresco.lib.rendition.internal.rendition.MemoryPromenaRenditionInProgressSynchronizer

@Configuration
class MemoryPromenaRenditionInProgressSynchronizerContext {

    @Bean
    fun memoryPromenaRenditionInProgressSynchronizer() =
        MemoryPromenaRenditionInProgressSynchronizer()
}