package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
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
		wgpuDeviceCreateRenderPipeline(handler, descriptor.convert())
			?.let(::RenderPipeline) ?: error("fail to create render pipeline")

	override fun close() {
		wgpuDeviceRelease(handler)
	}

}

private fun RenderPipelineDescriptor.VertexState.VertexBufferLayout.convert(): WGPUVertexBufferLayout.ByReference {
	TODO("Not yet implemented")
}

private fun RenderPipelineDescriptor.convert(): WGPURenderPipelineDescriptor = WGPURenderPipelineDescriptor().also {
	it.vertex.let { wGPUVertexState ->
		wGPUVertexState.module = vertex.module.handler
		wGPUVertexState.entryPoint = vertex.entryPoint
		wGPUVertexState.bufferCount = (vertex.buffers?.size ?: 0).toLong().let { NativeLong(it) }
		wGPUVertexState.buffers =
			vertex.buffers?.map { it.convert() }?.toTypedArray() ?: arrayOf(WGPUVertexBufferLayout.ByReference())
	}
	it.layout = layout?.handler
	it.label = label
	it.primitive.let { wgpuPrimitiveState ->
		wgpuPrimitiveState.topology = primitive?.topology?.value
		wgpuPrimitiveState.stripIndexFormat = primitive?.stripIndexFormat?.value
		wgpuPrimitiveState.frontFace = primitive?.frontFace?.value
		wgpuPrimitiveState.cullMode = primitive?.cullMode?.value
		// TODO find how to map this
		//wgpuPrimitiveState.unclippedDepth = primitive.unclippedDepth
	}

//	it.depthStencil = this@convert.depthStencil?.convert()
//	it.fragment = this@convert.fragment?.convert()
//	it.multisample = this@convert.multisample?.convert()

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
