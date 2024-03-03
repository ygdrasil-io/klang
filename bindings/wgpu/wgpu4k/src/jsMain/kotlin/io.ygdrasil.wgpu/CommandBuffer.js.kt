@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCommandBuffer

actual class CommandBuffer(internal val handler: GPUCommandBuffer) : AutoCloseable {
	override fun close() {
		// Nothing to do
	}
}