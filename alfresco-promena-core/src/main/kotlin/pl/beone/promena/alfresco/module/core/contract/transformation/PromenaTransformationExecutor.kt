package pl.beone.promena.alfresco.module.core.contract.transformation

import pl.beone.promena.alfresco.module.core.applicationmodel.node.NodeDescriptor
import pl.beone.promena.alfresco.module.core.applicationmodel.retry.Retry
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutor
import pl.beone.promena.transformer.contract.transformation.Transformation

/**
 * Constitutes the bridge between Alfresco Content Services and Promena. It constructs a transformation and delegates an execution to Promena.
 */
interface PromenaTransformationExecutor {

    /**
     * Executes [transformation] using [nodeDescriptor] on Promena asynchronously.
     * In case of an error, another execution is run until the number of [Retry.maxAttempts] is reached. If [retry] is `null`, it should use global settings.
     * In case of a successful result of a execution, [postTransformationExecutor] is run.
     *
     * @return the descriptor of the delegated execution to Promena
     */
    fun execute(
        transformation: Transformation,
        nodeDescriptor: NodeDescriptor,
        postTransformationExecutor: PostTransformationExecutor? = null,
        retry: Retry? = null
    ): TransformationExecution
}