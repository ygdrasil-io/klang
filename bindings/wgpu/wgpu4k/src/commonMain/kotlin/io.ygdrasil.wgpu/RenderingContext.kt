@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class RenderingContext: AutoCloseable {
	fun getCurrentTexture(): Texture?
}