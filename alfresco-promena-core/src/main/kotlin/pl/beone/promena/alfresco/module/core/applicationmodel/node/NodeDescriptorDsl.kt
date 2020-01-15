@file:JvmName("NodeDescriptorDsl")

package pl.beone.promena.alfresco.module.core.applicationmodel.node

import org.alfresco.service.cmr.repository.NodeRef
import pl.beone.promena.transformer.contract.model.Metadata
import pl.beone.promena.transformer.internal.model.metadata.emptyMetadata

fun singleNodeDescriptor(nodeRef: NodeRef, metadata: Metadata = emptyMetadata()): NodeDescriptor.Single =
    NodeDescriptor.Single.of(nodeRef, metadata)

fun NodeRef.toSingleNodeDescriptor(metadata: Metadata = emptyMetadata()): NodeDescriptor.Single =
    NodeDescriptor.Single.of(this, metadata)

/**
 * ```
 * singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "b0bfb14c-be38-48be-90c3-cae4a7fd0c8f"), <Metadata>) +
 *      singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "7abdf1e2-92f4-47b2-983a-611e42f3555c"), <Metadata>)
 * ```
 *
 * @return concatenation of `this` and [descriptor]
 */
operator fun NodeDescriptor.Single.plus(descriptor: NodeDescriptor.Single): NodeDescriptor.Multi =
    NodeDescriptor.Multi.of(descriptors + descriptor)

fun multiNodeDescriptor(descriptor: NodeDescriptor.Single, descriptors: List<NodeDescriptor.Single>): NodeDescriptor.Multi =
    NodeDescriptor.Multi.of(listOf(descriptor) + descriptors)

fun multiNodeDescriptor(descriptor: NodeDescriptor.Single, vararg descriptors: NodeDescriptor.Single): NodeDescriptor.Multi =
    multiNodeDescriptor(descriptor, descriptors.toList())

/**
 * ```
 * singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "7abdf1e2-92f4-47b2-983a-611e42f3555c"), <Metadata>) +
 *      singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "b0bfb14c-be38-48be-90c3-cae4a7fd0c8f"), <Metadata>) +
 *      singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "001700bd-532e-49bc-8e26-daa549145474"), <Metadata>)
 * ```
 *
 * @return concatenation of `this` and [descriptor]
 */
operator fun NodeDescriptor.Multi.plus(descriptor: NodeDescriptor.Single): NodeDescriptor.Multi =
    NodeDescriptor.Multi.of(descriptors + descriptor)

/**
 * ```
 * multiNodeDescriptor(singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "7abdf1e2-92f4-47b2-983a-611e42f3555c")) +
 *      multiNodeDescriptor(singleNodeDescriptor(NodeRef(STORE_REF_WORKSPACE_SPACESSTORE, "b0bfb14c-be38-48be-90c3-cae4a7fd0c8f"))
 * ```
 *
 * @return concatenation of `this` and [descriptor]
 */
operator fun NodeDescriptor.Multi.plus(descriptor: NodeDescriptor.Multi): NodeDescriptor.Multi =
    NodeDescriptor.Multi.of(descriptors + descriptor.descriptors)

/**
 * @return [NodeDescriptor.Single] if [descriptors] has one element
 *         and [NodeDescriptor.Multi] if [descriptors] has many elements
 * @throws IllegalArgumentException if an empty list is passed
 */
fun nodeDescriptor(descriptors: List<NodeDescriptor.Single>): NodeDescriptor =
    when (descriptors.size) {
        0 -> throw IllegalArgumentException("NodeDescriptor must consist of at least one descriptor")
        1 -> descriptors.first()
        else -> NodeDescriptor.Multi.of(descriptors.toList())
    }

/**
 * @see [nodeDescriptor]
 */
fun nodeDescriptor(vararg descriptors: NodeDescriptor.Single): NodeDescriptor =
    nodeDescriptor(descriptors.toList())

/**
 * @see [nodeDescriptor]
 */
fun List<NodeDescriptor.Single>.toNodeDescriptor(): NodeDescriptor =
    nodeDescriptor(this)

fun NodeDescriptor.toNodeRefs(): List<NodeRef> =
    this.descriptors.map(NodeDescriptor.Single::nodeRef)