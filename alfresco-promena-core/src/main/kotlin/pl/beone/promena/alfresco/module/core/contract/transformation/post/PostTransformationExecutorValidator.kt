package pl.beone.promena.alfresco.module.core.contract.transformation.post

interface PostTransformationExecutorValidator {

    /**
     * Validates the correctness of [postTransformationExecutor].
     */
    fun validate(postTransformationExecutor: PostTransformationExecutor)
}