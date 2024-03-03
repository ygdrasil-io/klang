package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
import io.ygdrasil.wgpu.internal.jvm.WGPUQueue
import io.ygdrasil.wgpu.internal.jvm.wgpuQueueSubmit

actual class Queue(internal val handler: WGPUQueue) {

	actual fun submit(commandsBuffer: Array<CommandBuffer>) {
		wgpuQueueSubmit(
			handler,
			NativeLong(commandsBuffer.size.toLong()),
			commandsBuffer.map { it.handler }.toTypedArray()
		)
	}

}