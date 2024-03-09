@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUBuffer

actual class Buffer(internal val handler: GPUBuffer) : AutoCloseable {

	actual fun getMappedRange(offset: GPUSize64, size: GPUSize64): Int {
		handler.getMappedRange(offset, size)
		TODO()
	}

	actual fun unmap() {
		handler.unmap()
	}

	override fun close() {
		//Nothing to do on JS
	}
}