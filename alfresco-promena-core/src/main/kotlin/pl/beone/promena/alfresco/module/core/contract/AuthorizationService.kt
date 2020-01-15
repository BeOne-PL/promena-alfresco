package pl.beone.promena.alfresco.module.core.contract

interface AuthorizationService {

    /**
     * Get the user name of the current user.
     */
    fun getCurrentUser(): String

    /**
     * Executes [toRun] with [userName] privileges.
     *
     * @return the result of [toRun]
     */
    fun <T> runAs(userName: String, toRun: () -> T): T
}