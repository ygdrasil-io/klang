package io.ygdrasil.wgpu

@OptIn(ExperimentalStdlibApi::class)
expect class Device: AutoCloseable {

	fun createCommandEncoder(descriptor: CommandEncoderDescriptor? = null): CommandEncoder?

}

// TODO
class CommandEncoderDescriptor