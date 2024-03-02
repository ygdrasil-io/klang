@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUDevice

actual class Device(val handler: GPUDevice): AutoCloseable {

	val queue: Queue by lazy { Queue(handler.queue) }

	override fun close() {
		// Nothing on JS
	}

	actual fun createCommandEncoder(): CommandEncoder? {
		return CommandEncoder(handler.createCommandEncoder())

	}
}