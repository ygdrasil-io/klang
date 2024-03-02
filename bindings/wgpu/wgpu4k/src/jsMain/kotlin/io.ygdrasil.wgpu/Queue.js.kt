package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCommandBuffer
import io.ygdrasil.wgpu.internal.js.GPUQueue

actual class Queue(private val handler: GPUQueue) {
	fun submit(commandsBuffer: Array<GPUCommandBuffer>) {
		handler.submit(commandsBuffer)
	}
}