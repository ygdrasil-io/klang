package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPUBuffer
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferGetMappedRange
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferRelease
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferUnmap

actual class Buffer(internal val handler: WGPUBuffer) : AutoCloseable {

	actual fun getMappedRange(offset: GPUSize64?, size: GPUSize64?): ByteArray {
		wgpuBufferGetMappedRange(handler, offset?.toNativeLong(), size?.toNativeLong())
		TODO()
	}

	actual fun unmap() {
		wgpuBufferUnmap(handler)
	}

	actual fun map(buffer: FloatArray) {
		(wgpuBufferGetMappedRange(handler, null, null) ?: error("fail to get mapped range"))
			.write(0L, buffer, 0, buffer.size)
	}

	override fun close() {
		wgpuBufferRelease(handler)
	}

}


