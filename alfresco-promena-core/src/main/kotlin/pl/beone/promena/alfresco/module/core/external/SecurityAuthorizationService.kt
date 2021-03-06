package pl.beone.promena.alfresco.module.core.external

import org.alfresco.repo.security.authentication.AuthenticationUtil
import org.alfresco.service.ServiceRegistry
import pl.beone.promena.alfresco.module.core.contract.AuthorizationService

/**
 * Uses [AuthenticationService][org.alfresco.service.cmr.security.AuthenticationService] to provide the functionality.
 */
class SecurityAuthorizationService(
    private val serviceRegistry: ServiceRegistry
) : AuthorizationService {

    override fun getCurrentUser(): String =
        serviceRegistry.authenticationService.currentUserName

    override fun <T> runAs(userName: String, toRun: () -> T): T =
        AuthenticationUtil.runAs({ toRun() }, userName)
}