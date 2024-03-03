package io.ygdrasil.wgpu

@OptIn(ExperimentalStdlibApi::class)
expect class Device: AutoCloseable {

	fun createCommandEncoder(descriptor: CommandEncoderDescriptor? = null): CommandEncoder
	fun createShaderModule(descriptor: ShaderModuleDescriptor)
	fun createPipelineLayout(): Any

}

// TODO
data class CommandEncoderDescriptor(var label: String? = null)