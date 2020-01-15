package pl.beone.promena.alfresco.module.core.applicationmodel.retry

import java.time.Duration

/**
 * Provides information about retrying mechanism.
 * [Retry.No] means that retrying mechanism is disabled.
 *
 * @see RetryDsl
 */
sealed class Retry {

    object No : Retry() {

        override val maxAttempts: Long
            get() = throw throwUnsupportedException()
        override val nextAttemptDelay: Duration
            get() = throw throwUnsupportedException()

        private fun throwUnsupportedException(): UnsupportedOperationException =
            UnsupportedOperationException("You can't get this value of <Retry.No> policy")
    }

    data class Custom internal constructor(
        override val maxAttempts: Long,
        override val nextAttemptDelay: Duration
    ) : Retry()

    /**
     * The maximum number of attempts.
     */
    abstract val maxAttempts: Long
    /**
     * The delay before next attempt.
     */
    abstract val nextAttemptDelay: Duration
}