@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class BindGroup : AutoCloseable {
}

data class BindGroupDescriptor(
	var layout: PipelineLayoutDescriptor.BindGroupLayout,
	var entries: Array<BindGroupEntry>,
	var label: String? = null
) {

	data class BindGroupEntry(
		var binding: GPUIndex32,
		var buffer: Buffer? = null,
		//var resource: Any
		// /* GPUSampler | GPUTextureView | GPUBufferBinding | GPUExternalTexture */
	)

}