package pl.beone.promena.alfresco.module.core.contract.transformation

import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecutionResult
import java.time.Duration
import java.util.concurrent.TimeoutException

/**
 * Provides information about the status of a transformation execution.
 */
interface PromenaTransformationManager {

    /**
     * Tries to get a result of [transformationExecution].
     * If a result isn't available, it waits a maximum of [waitMax] for a response. If [waitMax] is `null`, it should use global settings.
     *
     * @throws TimeoutException if [waitMax] (if it is present) or default timeout is exceeded
     * @throws IllegalStateException if there is no [transformationExecution] in progress
     * @throws Exception if [transformationExecution] is resulted in an exception
     */
    fun getResult(transformationExecution: TransformationExecution, waitMax: Duration? = null): TransformationExecutionResult

    interface PromenaMutableTransformationManager : PromenaTransformationManager {

        /**
         * Starts a new transformation.
         *
         * @return the descriptor of the new transformation
         */
        fun startTransformation(): TransformationExecution

        /**
         * Marks the [transformationExecution] as executed and saves the successful result ([result]).
         */
        fun completeTransformation(transformationExecution: TransformationExecution, result: TransformationExecutionResult)

        /**
         * Marks the [transformationExecution] as executed and saves the failed result ([throwable]).
         */
        fun completeErrorTransformation(transformationExecution: TransformationExecution, throwable: Throwable)
    }
}