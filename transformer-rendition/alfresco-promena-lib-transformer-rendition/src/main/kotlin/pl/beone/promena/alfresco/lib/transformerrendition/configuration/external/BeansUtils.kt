package pl.beone.promena.alfresco.lib.transformerrendition.configuration.external

import org.springframework.context.ApplicationContext
import pl.beone.promena.alfresco.module.core.contract.transformation.PromenaTransformationExecutor

internal fun ApplicationContext.getPromenaTransformationExecutor(beanName: String?): PromenaTransformationExecutor =
    if (beanName != null) {
        getBean(beanName, PromenaTransformationExecutor::class.java)
    } else {
        getBean(PromenaTransformationExecutor::class.java)
    }