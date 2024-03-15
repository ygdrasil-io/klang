package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import dev.krud.shapeshift.ShapeShiftBuilder
import dev.krud.shapeshift.enums.AutoMappingStrategy
import dev.krud.shapeshift.transformer.base.MappingTransformer
import dev.krud.shapeshift.transformer.base.MappingTransformerContext
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
		textureDescriptorMapper.map<TextureDescriptor, WGPUTextureDescriptor>(descriptor)
			.let { wgpuDeviceCreateTexture(handler, it) }
			?.let(::Texture) ?: error("fail to create texture")

	actual fun createSampler(descriptor: SamplerDescriptor): Sampler =
		samplerDescriptorMapper.map<SamplerDescriptor, WGPUSamplerDescriptor>(descriptor)
			.let { wgpuDeviceCreateSampler(handler, it) }
			?.let(::Sampler) ?: error("fail to create texture")

	override fun close() {
		wgpuDeviceRelease(handler)
	}

}

val textureDescriptorMapper = ShapeShiftBuilder()
	.withMapping<TextureDescriptor, WGPUTextureDescriptor> {
		autoMap(AutoMappingStrategy.BY_NAME)
		TextureDescriptor::format mappedTo WGPUTextureDescriptor::format withTransformer EnumerationTransformer()
		TextureDescriptor::dimension mappedTo WGPUTextureDescriptor::dimension withTransformer EnumerationTransformer()
		TextureDescriptor::size mappedTo WGPUTextureDescriptor::size withTransformer GPUExtent3DDictStrictTransformer()
	}
	.build()

val samplerDescriptorMapper = ShapeShiftBuilder()
	.withMapping<SamplerDescriptor, WGPUSamplerDescriptor> {
		autoMap(AutoMappingStrategy.BY_NAME)

	}
	.build()


private fun TextureDescriptor.convert(): WGPUTextureDescriptor = WGPUTextureDescriptor().also {
	it.size.apply {
		width = size.width
		height = size.height
		depthOrArrayLayers = size.depthOrArrayLayers
	}
	it.format = format.value
	it.usage = usage
	/* Iterable<GPUIntegerCoordinate> | GPUExtent3DDictStrict */
	it.mipLevelCount = mipLevelCount

	it.sampleCount = sampleCount
	it.dimension = dimension?.value
	/* "1d" | "2d" | "3d" */

	/* "r8unorm" | "r8snorm" | "r8uint" | "r8sint" | "r16uint" | "r16sint" | "r16float" | "rg8unorm" | "rg8snorm" | "rg8uint" | "rg8sint" | "r32uint" | "r32sint" | "r32float" | "rg16uint" | "rg16sint" | "rg16float" | "rgba8unorm" | "rgba8unorm-srgb" | "rgba8snorm" | "rgba8uint" | "rgba8sint" | "bgra8unorm" | "bgra8unorm-srgb" | "rgb9e5ufloat" | "rgb10a2uint" | "rgb10a2unorm" | "rg11b10ufloat" | "rg32uint" | "rg32sint" | "rg32float" | "rgba16uint" | "rgba16sint" | "rgba16float" | "rgba32uint" | "rgba32sint" | "rgba32float" | "stencil8" | "depth16unorm" | "depth24plus" | "depth24plus-stencil8" | "depth32float" | "depth32float-stencil8" | "bc1-rgba-unorm" | "bc1-rgba-unorm-srgb" | "bc2-rgba-unorm" | "bc2-rgba-unorm-srgb" | "bc3-rgba-unorm" | "bc3-rgba-unorm-srgb" | "bc4-r-unorm" | "bc4-r-snorm" | "bc5-rg-unorm" | "bc5-rg-snorm" | "bc6h-rgb-ufloat" | "bc6h-rgb-float" | "bc7-rgba-unorm" | "bc7-rgba-unorm-srgb" | "etc2-rgb8unorm" | "etc2-rgb8unorm-srgb" | "etc2-rgb8a1unorm" | "etc2-rgb8a1unorm-srgb" | "etc2-rgba8unorm" | "etc2-rgba8unorm-srgb" | "eac-r11unorm" | "eac-r11snorm" | "eac-rg11unorm" | "eac-rg11snorm" | "astc-4x4-unorm" | "astc-4x4-unorm-srgb" | "astc-5x4-unorm" | "astc-5x4-unorm-srgb" | "astc-5x5-unorm" | "astc-5x5-unorm-srgb" | "astc-6x5-unorm" | "astc-6x5-unorm-srgb" | "astc-6x6-unorm" | "astc-6x6-unorm-srgb" | "astc-8x5-unorm" | "astc-8x5-unorm-srgb" | "astc-8x6-unorm" | "astc-8x6-unorm-srgb" | "astc-8x8-unorm" | "astc-8x8-unorm-srgb" | "astc-10x5-unorm" | "astc-10x5-unorm-srgb" | "astc-10x6-unorm" | "astc-10x6-unorm-srgb" | "astc-10x8-unorm" | "astc-10x8-unorm-srgb" | "astc-10x10-unorm" | "astc-10x10-unorm-srgb" | "astc-12x10-unorm" | "astc-12x10-unorm-srgb" | "astc-12x12-unorm" | "astc-12x12-unorm-srgb" */
	it.viewFormatCount
	TODO()
	//it.viewFormats = viewFormats
	/* "r8unorm" | "r8snorm" | "r8uint" | "r8sint" | "r16uint" | "r16sint" | "r16float" | "rg8unorm" | "rg8snorm" | "rg8uint" | "rg8sint" | "r32uint" | "r32sint" | "r32float" | "rg16uint" | "rg16sint" | "rg16float" | "rgba8unorm" | "rgba8unorm-srgb" | "rgba8snorm" | "rgba8uint" | "rgba8sint" | "bgra8unorm" | "bgra8unorm-srgb" | "rgb9e5ufloat" | "rgb10a2uint" | "rgb10a2unorm" | "rg11b10ufloat" | "rg32uint" | "rg32sint" | "rg32float" | "rgba16uint" | "rgba16sint" | "rgba16float" | "rgba32uint" | "rgba32sint" | "rgba32float" | "stencil8" | "depth16unorm" | "depth24plus" | "depth24plus-stencil8" | "depth32float" | "depth32float-stencil8" | "bc1-rgba-unorm" | "bc1-rgba-unorm-srgb" | "bc2-rgba-unorm" | "bc2-rgba-unorm-srgb" | "bc3-rgba-unorm" | "bc3-rgba-unorm-srgb" | "bc4-r-unorm" | "bc4-r-snorm" | "bc5-rg-unorm" | "bc5-rg-snorm" | "bc6h-rgb-ufloat" | "bc6h-rgb-float" | "bc7-rgba-unorm" | "bc7-rgba-unorm-srgb" | "etc2-rgb8unorm" | "etc2-rgb8unorm-srgb" | "etc2-rgb8a1unorm" | "etc2-rgb8a1unorm-srgb" | "etc2-rgba8unorm" | "etc2-rgba8unorm-srgb" | "eac-r11unorm" | "eac-r11snorm" | "eac-rg11unorm" | "eac-rg11snorm" | "astc-4x4-unorm" | "astc-4x4-unorm-srgb" | "astc-5x4-unorm" | "astc-5x4-unorm-srgb" | "astc-5x5-unorm" | "astc-5x5-unorm-srgb" | "astc-6x5-unorm" | "astc-6x5-unorm-srgb" | "astc-6x6-unorm" | "astc-6x6-unorm-srgb" | "astc-8x5-unorm" | "astc-8x5-unorm-srgb" | "astc-8x6-unorm" | "astc-8x6-unorm-srgb" | "astc-8x8-unorm" | "astc-8x8-unorm-srgb" | "astc-10x5-unorm" | "astc-10x5-unorm-srgb" | "astc-10x6-unorm" | "astc-10x6-unorm-srgb" | "astc-10x8-unorm" | "astc-10x8-unorm-srgb" | "astc-10x10-unorm" | "astc-10x10-unorm-srgb" | "astc-12x10-unorm" | "astc-12x10-unorm-srgb" | "astc-12x12-unorm" | "astc-12x12-unorm-srgb" */

	it.label = label
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
		it.attributes = WGPUVertexAttribute.ByReference()
			.toArray(attributes.size)
			.let { it as Array<WGPUVertexAttribute.ByReference> }
			.also {
				it.forEachIndexed { index, structure -> structure.updateFrom(attributes[index]) }
			}

		it.stepMode = stepMode?.value
	}

private fun WGPUVertexAttribute.ByReference.updateFrom(vertexAttribute: RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute) {
	format = vertexAttribute.format.value
	offset = vertexAttribute.offset
	shaderLocation = vertexAttribute.shaderLocation
}

private fun RenderPipelineDescriptor.convert(): WGPURenderPipelineDescriptor = WGPURenderPipelineDescriptor().also {
	it.vertex = WGPUVertexState().also { wGPUVertexState ->
		wGPUVertexState.module = vertex.module.handler
		wGPUVertexState.entryPoint = vertex.entryPoint ?: "main"
		wGPUVertexState.bufferCount = (vertex.buffers?.size ?: 0).toNativeLong()
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
		it.entryPoint = entryPoint ?: "main"
		it.targetCount = targets.filterNotNull().size.toLong().let { NativeLong(it) }
		it.targets = targets.filterNotNull().map { it.convert() }.toTypedArray()
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.convert(): WGPUColorTargetState.ByReference =
	WGPUColorTargetState.ByReference().also {
		it.format = format.value
		it.blend = blend?.convert()
		it.writeMask = writeMask?.value
	}

private fun RenderPipelineDescriptor.FragmentState.ColorTargetState.BlendState.convert(): Pointer? {
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
