@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class Buffer : AutoCloseable {
	fun getMappedRange(offset: GPUSize64, size: GPUSize64): Int
	fun unmap()
}

data class BufferDescriptor(
	var size: GPUSize64,
	var usage: GPUBufferUsageFlags,
	var mappedAtCreation: Boolean? = null
)