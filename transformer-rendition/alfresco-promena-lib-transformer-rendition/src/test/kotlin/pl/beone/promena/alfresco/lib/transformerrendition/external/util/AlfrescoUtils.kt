package pl.beone.promena.alfresco.lib.transformerrendition.external.util

import org.alfresco.model.ContentModel
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.cmr.model.FileExistsException
import org.alfresco.service.cmr.repository.NodeRef
import java.time.LocalDateTime

fun ServiceRegistry.createOrGetIntegrationTestsFolder(): NodeRef =
    try {
        fileFolderService.create(getCompanyHomeNodeRef(), "Integration test", ContentModel.TYPE_FOLDER)
            .nodeRef
    } catch (e: FileExistsException) {
        fileFolderService.searchSimple(getCompanyHomeNodeRef(), "Integration test")
    }

fun ServiceRegistry.getCompanyHomeNodeRef(): NodeRef =
    nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null)

fun createNameBasedOnDate(): String =
    LocalDateTime.now().toString().replace(":", "_")