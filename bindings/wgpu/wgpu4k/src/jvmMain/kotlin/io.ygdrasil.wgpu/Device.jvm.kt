package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import io.ygdrasil.wgpu.internal.jvm.*

actual class Device(internal val handler: WGPUDeviceImpl) : AutoCloseable {

	actual val queue: Queue by lazy { Queue(wgpuDeviceGetQueue(handler) ?: error("fail to get device queue")) }

	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder =
		wgpuDeviceCreateCommandEncoder(handler, descriptor?.convert())
			?.let(::CommandEncoder) ?: error("fail to create command encoder")


	actual fun createShaderModule(descriptor: ShaderModuleDescriptor): ShaderModule =
		wgpuDeviceCreateShaderModule(handler, descriptor.convert())
			?.let(::ShaderModule) ?: error("fail to create shader module")


	actual fun createPipelineLayout(descriptor: PipelineLayoutDescriptor): PipelineLayout =
		wgpuDeviceCreatePipelineLayout(handler, descriptor.convert())
			?.let(::PipelineLayout) ?: error("fail to create pipeline layout")

	actual fun createRenderPipeline(descriptor: RenderPipelineDescriptor): RenderPipeline =
		descriptor.convert()
			.let { wgpuDeviceCreateRenderPipeline(handler, it) }
			?.let(::RenderPipeline) ?: error("fail to create render pipeline")

	actual fun createBuffer(descriptor: BufferDescriptor): Buffer =
		descriptor.convert()
			.let { wgpuDeviceCreateBuffer(handler, it) }
			?.let(::Buffer) ?: error("fail to create buffer")

	actual fun createBindGroup(descriptor: BindGroupDescriptor): BindGroup =
		descriptor.convert()
			.let { wgpuDeviceCreateBindGroup(handler, it) }
			?.let(::BindGroup) ?: error("fail to create bind group")

	actual fun createTexture(descriptor: TextureDescriptor): Texture =
		descriptor.convert()
			.let { wgpuDeviceCreateTexture(handler, it) }
			?.let(::Texture) ?: error("fail to create texture")

	actual fun createSampler(descriptor: SamplerDescriptor): Sampler =
		descriptor.convert()
			.let { wgpuDeviceCreateSampler(handler, it) }
			?.let(::Sampler) ?: error("fail to create texture")

	override fun close() {
		wgpuDeviceRelease(handler)
	}

}

private fun SamplerDescriptor.convert(): WGPUSamplerDescriptor? {
	TODO()
}

private fun TextureDescriptor.convert(): WGPUTextureDescriptor {
	TODO()
}

private fun BindGroupDescriptor.convert(): WGPUBindGroupDescriptor = WGPUBindGroupDescriptor().also {
	TODO()
}

private fun BufferDescriptor.convert(): WGPUBufferDescriptor = WGPUBufferDescriptor().also {
	it.usage = usage
	it.size = size
	it.mappedAtCreation = mappedAtCreation?.toInt()
}

private fun RenderPipelineDescriptor.VertexState.VertexBufferLayout.convert(): WGPUVertexBufferLayout.ByReference =
	WGPUVertexBufferLayout.ByReference().also {
		it.arrayStride = arrayStride
		it.attributeCount = attributes.size.toNativeLong()
		it.attributes = attributes.map { it.convert() }.toTypedArray()
		it.stepMode = stepMode?.value

	}

private fun RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute.convert(): WGPUVertexAttribute.ByReference =
	WGPUVertexAttribute.ByReference().also {
		it.format = format.value
		it.offset = offset
		it.shaderLocation = shaderLocation
}

private fun RenderPipelineDescriptor.convert(): WGPURenderPipelineDescriptor = WGPURenderPipelineDescriptor().also {
	it.vertex = WGPUVertexState().also { wGPUVertexState ->
		wGPUVertexState.module = vertex.module.handler
		wGPUVertexState.entryPoint = vertex.entryPoint ?: "main"
		wGPUVertexState.bufferCount = (vertex.buffers?.size ?: 0).toLong().let { NativeLong(it) }
		wGPUVertexState.buffers = if (wGPUVertexState.bufferCount.toLong() == 0L) {
			arrayOf(WGPUVertexBufferLayout.ByReference())
		} else {
			vertex.buffers?.map { it.convert() }?.toTypedArray()
		}
	}
	it.layout = layout?.handler
	it.label = label
	it.primitive = WGPUPrimitiveState().also { wgpuPrimitiveState ->
		wgpuPrimitiveState.topology = primitive?.topology?.value
		wgpuPrimitiveState.stripIndexFormat = primitive?.stripIndexFormat?.value
		wgpuPrimitiveState.frontFace = primitive?.frontFace?.value
		wgpuPrimitiveState.cullMode = primitive?.cullMode?.value
		// TODO find how to map this
		//wgpuPrimitiveState.unclippedDepth = primitive.unclippedDepth
	}


//	it.depthStencil = this@convert.depthStencil?.convert()
	it.fragment = fragment?.convert()

	it.multisample = WGPUMultisampleState().also { wgpuMultisampleState ->
		wgpuMultisampleState.count = multisample?.count
		wgpuMultisampleState.mask = multisample?.mask
		wgpuMultisampleState.alphaToCoverageEnabled = multisample?.alphaToCoverageEnabled?.let {
			if (it) 1 else 0
		}
	}
}

private fun RenderPipelineDescriptor.FragmentState.convert(): WGPUFragmentState.ByReference =
	WGPUFragmentState.ByReference().also {
		it.module = module.handler
		it.entryPoint = entryPoint
		it.targetCount = targets.filterNotNull().size.toLong().let { NativeLong(it) }
		it.targets = targets.filterNotNull().map { it.convert() }.toTypedArray()
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.convert(): WGPUColorTargetState.ByReference =
	WGPUColorTargetState.ByReference().also {
		it.format = format.value
		it.blend = blend?.convert()
		it.writeMask = writeMask?.value
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.BlendState?.convert(): Pointer? {
	TODO("Not yet implemented")
}

private fun PipelineLayoutDescriptor.convert(): WGPUPipelineLayoutDescriptor = WGPUPipelineLayoutDescriptor().also {
	it.label = label
	// TODO find how to map this
	//it.bindGroupLayoutCount = bindGroupLayouts.size.toLong().let { NativeLong(it) }
	//it.bindGroupLayouts = bindGroupLayouts.map { it.convert() }.toTypedArray()
}

private fun CommandEncoderDescriptor.convert(): WGPUCommandEncoderDescriptor = WGPUCommandEncoderDescriptor().also {
	it.label = label
}
