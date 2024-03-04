package io.ygdrasil.wgpu

@OptIn(ExperimentalStdlibApi::class)
expect class Device: AutoCloseable {

	val queue: Queue

	fun createCommandEncoder(descriptor: CommandEncoderDescriptor? = null): CommandEncoder

	fun createShaderModule(descriptor: ShaderModuleDescriptor): ShaderModule

	fun createPipelineLayout(descriptor: PipelineLayoutDescriptor): PipelineLayout

	fun createRenderPipeline(descriptor: RenderPipelineDescriptor): RenderPipeline
}

// TODO
data class CommandEncoderDescriptor(var label: String? = null)