@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class Texture: AutoCloseable {

	fun createView(descriptor: TextureViewDescriptor? = null): TextureView
}