@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class BindGroup : AutoCloseable {
}

data class BindGroupDescriptor(
	var layout: BindGroupLayout,
	var entries: Array<BindGroupEntry>,
	var label: String? = null
) {

	data class BindGroupEntry(
		var binding: GPUIndex32,
		var resource: Resource
		// /* GPUSampler | GPUTextureView | GPUBufferBinding | GPUExternalTexture */
	) {
		sealed interface Resource
		data class BufferBinding(
			var buffer: Buffer,
			var offset: GPUSize64? = null,
			var size: GPUSize64? = null
		) : Resource

	}

}