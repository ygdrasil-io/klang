package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUQueue
import org.khronos.webgl.Float32Array

@JsExport
actual class Queue(private val handler: GPUQueue) {
	actual fun submit(commandsBuffer: Array<CommandBuffer>) {
		handler.submit(commandsBuffer.map { it.handler }.toTypedArray())
	}

	actual fun writeBuffer(
		buffer: Buffer,
		bufferOffset: GPUSize64,
		data: FloatArray,
		dataOffset: GPUSize64,
		size: GPUSize64
	) {
		handler.writeBuffer(
			buffer.handler,
			bufferOffset,
			data.unsafeCast<Float32Array>(),
			dataOffset,
			size
		)
	}
}