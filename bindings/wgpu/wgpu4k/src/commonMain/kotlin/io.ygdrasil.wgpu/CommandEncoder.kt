@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class CommandEncoder : AutoCloseable {

	fun beginRenderPass(renderPassDescriptor: RenderPassDescriptor): RenderPassEncoder

	fun finish(): CommandBuffer
}