package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPUSampler

actual class Sampler(wgpuDeviceCreateSampler: WGPUSampler?) : AutoCloseable {
	override fun close() {
		TODO("Not yet implemented")
	}
}