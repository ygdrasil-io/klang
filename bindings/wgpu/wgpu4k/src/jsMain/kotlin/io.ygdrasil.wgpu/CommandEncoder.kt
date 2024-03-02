package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.*

actual class CommandEncoder(private val commandEncoder: GPUCommandEncoder) {
	fun beginRenderPass(renderPassDescriptor: RenderPassDescriptor): RenderPassEncoder {
		return RenderPassEncoder(commandEncoder.beginRenderPass(renderPassDescriptor.convert()))
	}

	fun finish(): GPUCommandBuffer {
		return commandEncoder.finish()
	}
}

private fun RenderPassDescriptor.convert(): GPURenderPassDescriptor = object : GPURenderPassDescriptor {
	override var colorAttachments: Array<GPURenderPassColorAttachment> =
		this@convert.colorAttachments.map { it.convert() }.toTypedArray()
	// TODO implement
	/*var depthStencilAttachment: GPURenderPassDepthStencilAttachment?
	var occlusionQuerySet: GPUQuerySet?
	var timestampWrites: GPURenderPassTimestampWrites?
	var maxDrawCount: GPUSize64?*/
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
