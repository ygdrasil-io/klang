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

/*** RenderPipelineDescriptor ***/

private fun RenderPipelineDescriptor.convert(): GPURenderPipelineDescriptor = object : GPURenderPipelineDescriptor {
	override var vertex: GPUVertexState = this@convert.vertex.convert()
	override var layout: dynamic = this@convert.layout?.handler ?: "auto"
	override var label: dynamic = this@convert.label ?: undefined
	override var primitive: GPUPrimitiveState? = this@convert.primitive?.convert() ?: undefined
	override var depthStencil: GPUDepthStencilState? = this@convert.depthStencil?.convert() ?: undefined
	override var fragment: GPUFragmentState? = this@convert.fragment?.convert() ?: undefined
	override var multisample: GPUMultisampleState? = this@convert.multisample?.convert() ?: undefined
}

private fun RenderPipelineDescriptor.VertexState.convert(): GPUVertexState =
	object : GPUVertexState {
		override var module: GPUShaderModule = this@convert.module.handler
		override var entryPoint: String? = this@convert.entryPoint ?: undefined

		//TODO check mapping
		//override var constants: Map<String, GPUPipelineConstantValue>? = null
		override var buffers: Array<GPUVertexBufferLayout?>? = this@convert.buffers
			?.map { it?.convert() }?.toTypedArray() ?: undefined
	}

private fun RenderPipelineDescriptor.VertexState.VertexBufferLayout.convert(): GPUVertexBufferLayout =
	object : GPUVertexBufferLayout {
		override var arrayStride: GPUSize64 = this@convert.arrayStride
		override var attributes: Array<GPUVertexAttribute> = this@convert.attributes
			.map { it.convert() }.toTypedArray()
		override var stepMode: String? = this@convert.stepMode?.name ?: undefined
	}

private fun RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute.convert(): GPUVertexAttribute =
	object : GPUVertexAttribute {
		override var format: String = this@convert.format
		override var offset: GPUSize64 = this@convert.offset
		override var shaderLocation: GPUIndex32 = this@convert.shaderLocation
	}

private fun RenderPipelineDescriptor.PrimitiveState.convert(): GPUPrimitiveState =
	object : GPUPrimitiveState {
		override var topology: String? = this@convert.topology?.stringValue ?: undefined
		override var stripIndexFormat: String? = this@convert.stripIndexFormat ?: undefined
		override var frontFace: String? = this@convert.frontFace ?: undefined
		override var cullMode: String? = this@convert.cullMode ?: undefined
		override var unclippedDepth: Boolean? = this@convert.unclippedDepth ?: undefined
	}

private fun RenderPipelineDescriptor.DepthStencilState.convert(): GPUDepthStencilState =
	object : GPUDepthStencilState {
		override var format: String = this@convert.format.name
		override var depthWriteEnabled: Boolean? = this@convert.depthWriteEnabled ?: undefined
		override var depthCompare: String? = this@convert.depthCompare ?: undefined
		override var stencilFront: GPUStencilFaceState? = this@convert.stencilFront?.convert() ?: undefined
		override var stencilBack: GPUStencilFaceState? = this@convert.stencilBack?.convert() ?: undefined
		override var stencilReadMask: GPUStencilValue? = this@convert.stencilReadMask ?: undefined
		override var stencilWriteMask: GPUStencilValue? = this@convert.stencilWriteMask ?: undefined
		override var depthBias: GPUDepthBias? = this@convert.depthBias ?: undefined
		override var depthBiasSlopeScale: Float? = this@convert.depthBiasSlopeScale ?: undefined
		override var depthBiasClamp: Float? = this@convert.depthBiasClamp ?: undefined
	}

private fun RenderPipelineDescriptor.DepthStencilState.StencilFaceState.convert(): GPUStencilFaceState =
	object : GPUStencilFaceState {
		override var compare: String? = this@convert.compare ?: undefined
		override var failOp: String? = this@convert.failOp ?: undefined
		override var depthFailOp: String? = this@convert.depthFailOp ?: undefined
		override var passOp: String? = this@convert.passOp ?: undefined
	}

private fun RenderPipelineDescriptor.MultisampleState.convert(): GPUMultisampleState =
	object : GPUMultisampleState {
		override var count: dynamic = this@convert.count ?: undefined
		override var mask: dynamic = this@convert.mask ?: undefined
		override var alphaToCoverageEnabled: dynamic = this@convert.alphaToCoverageEnabled ?: undefined
	}

private fun RenderPipelineDescriptor.FragmentState.convert(): GPUFragmentState =
	object : GPUFragmentState {
		override var targets: Array<GPUColorTargetState?> = this@convert.targets.map { it?.convert() }.toTypedArray()
		override var module: GPUShaderModule = this@convert.module.handler
		override var entryPoint: String? = this@convert.entryPoint ?: undefined
		// TODO not sure how to map this
		//override var constants: Record<String, GPUPipelineConstantValue>? = TODO("Not yet implemented")
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.convert(): GPUColorTargetState =
	object : GPUColorTargetState {
		override var format: String = this@convert.format.name
		override var blend: GPUBlendState? = this@convert.blend?.convert() ?: undefined
		override var writeMask: GPUColorWriteFlags? = this@convert.writeMask?.value ?: undefined
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.BlendState.convert(): GPUBlendState =
	object : GPUBlendState {
		override var color: GPUBlendComponent = this@convert.color.convert()
		override var alpha: GPUBlendComponent = this@convert.alpha.convert()
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.BlendState.BlendComponent.convert(): GPUBlendComponent =
	object : GPUBlendComponent {
		override var operation: String? = this@convert.operation ?: undefined
		override var srcFactor: String? = this@convert.srcFactor ?: undefined
		override var dstFactor: String? = this@convert.dstFactor ?: undefined
	}

/*** PipelineLayoutDescriptor ***/

private fun PipelineLayoutDescriptor.convert(): GPUPipelineLayoutDescriptor = object : GPUPipelineLayoutDescriptor {
	override var label: String? = this@convert.label ?: undefined
	override var bindGroupLayouts: Array<GPUBindGroupLayout> = this@convert.bindGroupLayouts
		.map { it.convert() }.toTypedArray()
}

private fun PipelineLayoutDescriptor.BindGroupLayout.convert(): GPUBindGroupLayout =
	object : GPUBindGroupLayout {
		override var label: String = this@convert.label
		override var __brand: String = this@convert.__brand
	}

private fun CommandEncoderDescriptor.convert(): GPUCommandEncoderDescriptor = object : GPUCommandEncoderDescriptor {
	override var label: String? = this@convert.label ?: undefined
}

