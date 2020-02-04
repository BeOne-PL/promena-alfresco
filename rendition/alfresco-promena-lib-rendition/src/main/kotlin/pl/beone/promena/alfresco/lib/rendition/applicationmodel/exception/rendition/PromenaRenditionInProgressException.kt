package pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.rendition

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution

/**
 * Signals that [renditionName] transformation execution of [nodeRef] is in progress.
 */
class PromenaRenditionInProgressException(
    val nodeRef: NodeRef,
    val renditionName: String,
    val transformationExecution: TransformationExecution
) : IllegalStateException("Creating rendition <$renditionName> of <$nodeRef> is in progress in transformation <${transformationExecution.id}>...")