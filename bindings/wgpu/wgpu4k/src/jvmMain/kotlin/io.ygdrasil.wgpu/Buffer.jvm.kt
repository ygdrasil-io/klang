package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPUBuffer
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferGetMappedRange
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferRelease
import io.ygdrasil.wgpu.internal.jvm.wgpuBufferUnmap

actual class Buffer(protected val handler: WGPUBuffer) : AutoCloseable {

	actual fun getMappedRange(offset: GPUSize64?, size: GPUSize64?): Int {
		wgpuBufferGetMappedRange(handler, offset?.toNativeLong(), size?.toNativeLong())
		TODO()
	}

	actual fun unmap() {
		wgpuBufferUnmap(handler)
	}

	override fun close() {
		wgpuBufferRelease(handler)
	}

}


