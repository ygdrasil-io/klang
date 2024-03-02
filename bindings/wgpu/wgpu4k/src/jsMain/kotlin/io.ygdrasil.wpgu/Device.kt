@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUDevice
import io.ygdrasil.wgpu.internal.js.GPUQueue
import io.ygdrasil.wgpu.CommandEncoder

actual class Device(val handler: GPUDevice): AutoCloseable {

	val queue: GPUQueue = handler.queue

	override fun close() {
		// Nothing on JS
	}

	actual fun createCommandEncoder(): CommandEncoder? {
		return CommandEncoder(handler.createCommandEncoder())

	}
}