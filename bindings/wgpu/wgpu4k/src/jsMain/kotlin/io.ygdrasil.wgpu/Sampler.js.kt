@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUSampler

actual class Sampler(createSampler: GPUSampler) : AutoCloseable {
	override fun close() {
		// Nothing to do on JS
	}
}