package pl.beone.promena.alfresco.lib.rendition.contract.rendition

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.alfresco.lib.rendition.applicationmodel.exception.rendition.PromenaRenditionInProgressException
import pl.beone.promena.alfresco.module.core.applicationmodel.transformation.TransformationExecution

/**
 * Provides information about the status of a rendition transformation execution.
 * The aim of a implementation of this interface is to ensure only one rendition transformation execution for a node.
 */
interface PromenaRenditionInProgressSynchronizer {

    /**
     * Marks [renditionName] transformation execution for [nodeRef] as started.
     */
    fun start(nodeRef: NodeRef, renditionName: String, transformationExecution: TransformationExecution)

    /**
     * Marks [renditionName] transformation execution as executed.
     */
    fun finish(nodeRef: NodeRef, renditionName: String)

    /**
     * @throws PromenaRenditionInProgressException if [renditionName] transformation execution of [nodeRef] is in progress
     */
    fun isInProgress(nodeRef: NodeRef, renditionName: String)
}