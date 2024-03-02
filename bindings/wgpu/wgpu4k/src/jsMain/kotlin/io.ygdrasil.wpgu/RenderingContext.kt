@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

actual class RenderingContext : AutoCloseable {
	override fun close() {
		// Nothing to do on js
	}
}