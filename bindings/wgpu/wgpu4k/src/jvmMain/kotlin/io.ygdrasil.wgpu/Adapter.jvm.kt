package io.ygdrasil.wgpu

actual class Adapter : AutoCloseable {
	actual suspend fun requestDevice(): Device? {
		TODO("Not yet implemented")
	}

	override fun close() {
		TODO("Not yet implemented")
	}
}