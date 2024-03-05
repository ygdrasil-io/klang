@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class RenderPassEncoder: AutoCloseable {

	fun end()

	fun setPipeline(renderPipeline: RenderPipeline)

	fun draw(vertexCount: GPUSize32, instanceCount: GPUSize32, firstVertex: GPUSize32, firstInstance: GPUSize32)

}