@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPURenderPassEncoder

actual class RenderPassEncoder(private val handler: GPURenderPassEncoder) : AutoCloseable {

	actual fun end() {
		handler.end()
	}

	override fun close() {
		// Nothing to do
	}
}