package pl.beone.promena.alfresco.module.core.contract.transformation.post

import org.alfresco.service.ServiceRegistry
import org.springframework.context.ApplicationContext
import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecutionResult
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * The base class for every post-transformation operations.
 * If you want to provide custom operations, you has to extend this class.
 */
abstract class PostTransformationExecutor {

    protected lateinit var applicationContext: ApplicationContext
    protected lateinit var serviceRegistry: ServiceRegistry

    abstract fun execute(transformation: Transformation, nodeDescriptor: NodeDescriptor, result: TransformationExecutionResult)
}
