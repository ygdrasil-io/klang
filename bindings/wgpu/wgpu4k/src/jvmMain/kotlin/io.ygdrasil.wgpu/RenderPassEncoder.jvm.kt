package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPURenderPassEncoder
import io.ygdrasil.wgpu.internal.jvm.wgpuRenderPassEncoderEnd
import io.ygdrasil.wgpu.internal.jvm.wgpuRenderPassEncoderRelease

actual class RenderPassEncoder(private val handler: WGPURenderPassEncoder) : AutoCloseable {
	actual fun end() {
		wgpuRenderPassEncoderEnd(handler)
	}

	override fun close() {
		wgpuRenderPassEncoderRelease(handler)
	}

}