package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCommandBuffer
import io.ygdrasil.wgpu.internal.js.GPUCommandEncoder
import io.ygdrasil.wgpu.internal.js.GPURenderPassDescriptor
import io.ygdrasil.wgpu.internal.js.GPURenderPassEncoder

actual class CommandEncoder(private val commandEncoder: GPUCommandEncoder) {
	fun beginRenderPass(renderPassDescriptor: GPURenderPassDescriptor): GPURenderPassEncoder {
		return commandEncoder.beginRenderPass(renderPassDescriptor)
	}

	fun finish(): GPUCommandBuffer {
		return commandEncoder.finish()
	}
}