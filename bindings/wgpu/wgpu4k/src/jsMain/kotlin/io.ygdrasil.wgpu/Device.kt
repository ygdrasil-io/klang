@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.*

actual class Device(val handler: GPUDevice) : AutoCloseable {

	actual val queue: Queue by lazy { Queue(handler.queue) }

	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder {
		return CommandEncoder(
			when (descriptor) {
				null -> handler.createCommandEncoder()
				else -> handler.createCommandEncoder(descriptor.convert())
			}

		)
	}

	actual fun createShaderModule(descriptor: ShaderModuleDescriptor): ShaderModule {
		return ShaderModule(handler.createShaderModule(descriptor.convert()))
	}

	actual fun createPipelineLayout(descriptor: PipelineLayoutDescriptor): PipelineLayout = handler
		.createPipelineLayout(descriptor.convert())
		.let(::PipelineLayout)

	actual fun createRenderPipeline(descriptor: RenderPipelineDescriptor): RenderPipeline = handler
		.createRenderPipeline(descriptor.convert())
		.let(::RenderPipeline)


	override fun close() {
		// Nothing on JS
	}
}

private fun RenderPipelineDescriptor.convert(): GPURenderPipelineDescriptor = object : GPURenderPipelineDescriptor {

	override var vertex: GPUVertexState = TODO("Not yet implemented")
	override var primitive: GPUPrimitiveState? = TODO("Not yet implemented")
	override var depthStencil: GPUDepthStencilState? = TODO("Not yet implemented")
	override var multisample: GPUMultisampleState? = TODO("Not yet implemented")
	override var fragment: GPUFragmentState? = this@convert.fragment?.convert() ?: undefined
	override var layout: dynamic = this@convert.layout ?: "auto"
}

private fun RenderPipelineDescriptor.FragmentState.convert(): GPUFragmentState = object : GPUFragmentState {

	override var targets: Array<GPUColorTargetState?> = this@convert.targets.map { it?.convert() }.toTypedArray()
	override var module: GPUShaderModule = this@convert.module.handler
	override var entryPoint: String? = this@convert.entryPoint ?: undefined
	// TODO not sure how to map this
	//override var constants: Record<String, GPUPipelineConstantValue>? = TODO("Not yet implemented")
}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.convert(): GPUColorTargetState =
	object : GPUColorTargetState {
		override var format: String = TODO("Not yet implemented")
		override var blend: GPUBlendState? = TODO("Not yet implemented")
		override var writeMask: GPUColorWriteFlags? = TODO("Not yet implemented")
	}

private fun PipelineLayoutDescriptor.convert(): GPUPipelineLayoutDescriptor = object : GPUPipelineLayoutDescriptor {
	override var label: String? = TODO("Not yet implemented")
	override var bindGroupLayouts: Iterable<GPUBindGroupLayout> = TODO("Not yet implemented")
}

private fun CommandEncoderDescriptor.convert(): GPUCommandEncoderDescriptor = object : GPUCommandEncoderDescriptor {
	//TODO
}

