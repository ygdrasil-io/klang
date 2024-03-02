@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCommandEncoderDescriptor
import io.ygdrasil.wgpu.internal.js.GPUDevice

actual class Device(val handler: GPUDevice): AutoCloseable {

	val queue: Queue by lazy { Queue(handler.queue) }

	override fun close() {
		// Nothing on JS
	}

	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder? {
		return CommandEncoder(
			when (descriptor) {
				null -> handler.createCommandEncoder()
				else -> handler.createCommandEncoder(descriptor.convert())
			}

		)

	}
}

private fun CommandEncoderDescriptor.convert(): GPUCommandEncoderDescriptor = object : GPUCommandEncoderDescriptor {

}

