package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPURenderPassEncoder
import io.ygdrasil.wgpu.internal.jvm.wgpuRenderPassEncoderEnd
import io.ygdrasil.wgpu.internal.jvm.wgpuRenderPassEncoderRelease

actual class RenderPassEncoder(private val handler: WGPURenderPassEncoder) : AutoCloseable {
	actual fun end() {
		wgpuRenderPassEncoderEnd(handler)
	}

	actual fun setPipeline(renderPipeline: RenderPipeline) {
		TODO("not yet implemented")
	}

	actual fun draw(
		vertexCount: GPUSize32,
		instanceCount: GPUSize32,
		firstVertex: GPUSize32,
		firstInstance: GPUSize32
	) {
		TODO("not yet implemented")
	}

	override fun close() {
		wgpuRenderPassEncoderRelease(handler)
	}

}