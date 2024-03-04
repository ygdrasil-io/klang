package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class Device(internal val handler: WGPUDeviceImpl) : AutoCloseable {

	actual val queue: Queue by lazy { Queue(wgpuDeviceGetQueue(handler) ?: error("fail to get device queue")) }

	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder {
		return CommandEncoder(
			wgpuDeviceCreateCommandEncoder(handler, descriptor?.convert() ?: null)
				?: error("fail to create command encoder")
		)
	}

	actual fun createShaderModule(descriptor: ShaderModuleDescriptor): ShaderModule =
		wgpuDeviceCreateShaderModule(handler, descriptor.convert())
			?.let(::ShaderModule) ?: error("fail to create pipeline layout")


	actual fun createPipelineLayout(descriptor: PipelineLayoutDescriptor): PipelineLayout =
		wgpuDeviceCreatePipelineLayout(handler, descriptor.convert())
			?.let(::PipelineLayout) ?: error("fail to create pipeline layout")

	actual fun createRenderPipeline(descriptor: RenderPipelineDescriptor): RenderPipeline =
		wgpuDeviceCreateRenderPipeline(handler, descriptor.convert())
			?.let(::RenderPipeline) ?: error("fail to create pipeline layout")

	override fun close() {
		wgpuDeviceRelease(handler)
	}

}

private fun RenderPipelineDescriptor.convert(): WGPURenderPipelineDescriptor {
	TODO("Not yet implemented")

}

private fun PipelineLayoutDescriptor.convert(): WGPUPipelineLayoutDescriptor {
	TODO("Not yet implemented")
}

private fun CommandEncoderDescriptor.convert(): WGPUCommandEncoderDescriptor = WGPUCommandEncoderDescriptor().also {
	it.label = label
}
