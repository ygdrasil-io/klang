package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUQueue

actual class Queue(private val handler: GPUQueue) {
	fun submit(commandsBuffer: Array<CommandBuffer>) {
		handler.submit(commandsBuffer.map { it.handler }.toTypedArray())
	}
}