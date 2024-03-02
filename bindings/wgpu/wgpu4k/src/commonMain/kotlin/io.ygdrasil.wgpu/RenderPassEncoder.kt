@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class RenderPassEncoder: AutoCloseable {

	fun end()
}