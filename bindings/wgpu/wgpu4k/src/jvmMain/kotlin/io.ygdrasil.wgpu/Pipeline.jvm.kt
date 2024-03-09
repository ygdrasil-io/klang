package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class PipelineLayout(internal val handler: WGPUPipelineLayout)

actual class RenderPipeline(internal val handler: WGPURenderPipeline) : AutoCloseable {

	actual fun getBindGroupLayout(index: Int): PipelineLayoutDescriptor.BindGroupLayout =
		wgpuRenderPipelineGetBindGroupLayout(handler, index)
			?.convert() ?: error("fail to get bindgroup layout")

	override fun close() {
		wgpuRenderPipelineRelease(handler)
	}

}

private fun WGPUBindGroupLayoutImpl.convert(): PipelineLayoutDescriptor.BindGroupLayout {
	TODO("Not yet implemented")
}