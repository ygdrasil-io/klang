package io.ygdrasil.wgpu

expect class Buffer {
}

data class BufferDescriptor(
	var size: GPUSize64,
	var usage: GPUBufferUsageFlags,
	var mappedAtCreation: Boolean?
)