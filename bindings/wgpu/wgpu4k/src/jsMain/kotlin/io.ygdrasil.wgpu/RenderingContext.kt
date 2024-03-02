@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCanvasContext

actual class RenderingContext(private val handler: GPUCanvasContext) : AutoCloseable {
	override fun close() {
		// Nothing to do on js
	}

	actual fun getCurrentTexture(): Texture? {
		return Texture(handler.getCurrentTexture())
	}
}