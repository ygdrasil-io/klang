@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.*

actual class CommandEncoder(private val handler: GPUCommandEncoder) : AutoCloseable {
	actual fun beginRenderPass(renderPassDescriptor: RenderPassDescriptor): RenderPassEncoder {
		return RenderPassEncoder(handler.beginRenderPass(renderPassDescriptor.convert()))
	}

	actual fun finish(): CommandBuffer {
		return CommandBuffer(handler.finish())
	}

	override fun close() {
		// Nothing to do
	}
}

private fun RenderPassDescriptor.convert(): GPURenderPassDescriptor = object : GPURenderPassDescriptor {
	override var colorAttachments: Array<GPURenderPassColorAttachment> =
		this@convert.colorAttachments.map { it.convert() }.toTypedArray()
	override var label: String? = this@convert.label ?: undefined
	override var depthStencilAttachment: GPURenderPassDepthStencilAttachment? =
		this@convert.depthStencilAttachment?.convert() ?: undefined
	/*
	override var occlusionQuerySet: GPUQuerySet?
	override var timestampWrites: GPURenderPassTimestampWrites?
	*/
	override var maxDrawCount: GPUSize64? = this@convert.maxDrawCount ?: undefined
}

private fun RenderPassDescriptor.RenderPassDepthStencilAttachment.convert(): GPURenderPassDepthStencilAttachment =
	object : GPURenderPassDepthStencilAttachment {
		override var view: GPUTextureView = this@convert.view.handler
		override var depthClearValue: Number? = this@convert.depthClearValue ?: undefined

		/* "load" | "clear" */
		override var depthLoadOp: String? = this@convert.depthLoadOp?.name ?: undefined

		/* "store" | "discard" */
		override var depthStoreOp: String? = this@convert.depthStoreOp?.name ?: undefined
		override var depthReadOnly: Boolean? = this@convert.depthReadOnly ?: undefined
		override var stencilClearValue: GPUStencilValue? = this@convert.stencilClearValue ?: undefined

		/* "load" | "clear" */
		override var stencilLoadOp: String? = this@convert.stencilLoadOp?.name ?: undefined

		/* "store" | "discard" */
		override var stencilStoreOp: String? = this@convert.stencilStoreOp?.name ?: undefined
		override var stencilReadOnly: Boolean? = this@convert.stencilReadOnly ?: undefined

	}

private fun RenderPassDescriptor.ColorAttachment.convert(): GPURenderPassColorAttachment =
	object : GPURenderPassColorAttachment {
		override var view: GPUTextureView = this@convert.view.handler
		override var loadOp: String = this@convert.loadOp
		override var storeOp: String = this@convert.storeOp
		override var depthSlice: GPUIntegerCoordinate? = this@convert.depthSlice ?: undefined
		override var resolveTarget: GPUTextureView? = this@convert.resolveTarget?.handler ?: undefined
		override var clearValue: Array<Number>? = this@convert.clearValue ?: undefined
	}
