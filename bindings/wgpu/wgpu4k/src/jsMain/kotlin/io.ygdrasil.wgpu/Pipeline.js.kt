@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUBindGroupLayout
import io.ygdrasil.wgpu.internal.js.GPUPipelineLayout
import io.ygdrasil.wgpu.internal.js.GPURenderPipeline

actual class PipelineLayout(internal var handler: GPUPipelineLayout)
actual class RenderPipeline(internal var handler: GPURenderPipeline) : AutoCloseable {

	actual fun getBindGroupLayout(index: Int) = handler
		.getBindGroupLayout(index)
		.convert()


	override fun close() {
		// Nothing to do on js
	}

}

private fun GPUBindGroupLayout.convert(): PipelineLayoutDescriptor.BindGroupLayout {
	TODO("Not yet implemented")
}
