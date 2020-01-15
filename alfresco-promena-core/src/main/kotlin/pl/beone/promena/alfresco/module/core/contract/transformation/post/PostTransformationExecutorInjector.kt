package pl.beone.promena.alfresco.module.core.contract.transformation.post

interface PostTransformationExecutorInjector {

    /**
     * Detects and injects dependencies into [postTransformationExecutor].
     * It injects at least [PostTransformationExecutor.applicationContext] and [PostTransformationExecutor.serviceRegistry] dependencies.
     */
    fun inject(postTransformationExecutor: PostTransformationExecutor)
}