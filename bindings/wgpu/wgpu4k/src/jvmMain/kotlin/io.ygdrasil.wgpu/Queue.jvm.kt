package io.ygdrasil.wgpu

import com.sun.jna.Memory
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import io.ygdrasil.wgpu.internal.jvm.WGPUQueue
import io.ygdrasil.wgpu.internal.jvm.wgpuQueueSubmit
import io.ygdrasil.wgpu.internal.jvm.wgpuQueueWriteBuffer

actual class Queue(internal val handler: WGPUQueue) {

	actual fun submit(commandsBuffer: Array<CommandBuffer>) {
		wgpuQueueSubmit(
			handler,
			NativeLong(commandsBuffer.size.toLong()),
			commandsBuffer.map { it.handler }.toTypedArray()
		)
	}

	actual fun writeBuffer(
		buffer: Buffer,
		bufferOffset: GPUSize64,
		data: FloatArray,
		dataOffset: GPUSize64,
		size: GPUSize64
	) {
		wgpuQueueWriteBuffer(
			handler,
			buffer.handler,
			bufferOffset,
			data.toBuffer(dataOffset),
			size.toNativeLong()
		)
	}

	private fun FloatArray.toBuffer(dataOffset: GPUSize64): Pointer? {
		//Multiply by 4 because of 4 bytes per float
		return Memory(size * 4L).apply {
			write(0L, this@toBuffer, dataOffset.toInt(), size)
		}
	}

}

