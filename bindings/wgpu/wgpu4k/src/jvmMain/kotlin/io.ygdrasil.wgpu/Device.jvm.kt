package io.ygdrasil.wgpu

actual class Device : AutoCloseable {
	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder? {
		TODO("Not yet implemented")
	}

	override fun close() {
		TODO("Not yet implemented")
	}

}