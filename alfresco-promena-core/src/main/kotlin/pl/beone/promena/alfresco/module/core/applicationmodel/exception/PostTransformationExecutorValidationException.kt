package pl.beone.promena.alfresco.module.core.applicationmodel.exception

/**
 * Signals that a implementation of
 * [PostTransformationExecutor][pl.beone.promena.alfresco.module.core.contract.transformation.post.PostTransformationExecutor] isn't correct
 */
class PostTransformationExecutorValidationException(
    message: String,
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)