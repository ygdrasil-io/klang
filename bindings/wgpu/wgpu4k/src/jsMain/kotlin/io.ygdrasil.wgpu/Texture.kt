@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUTexture
import io.ygdrasil.wgpu.internal.js.GPUTextureViewDescriptor

@JsExport
actual class Texture(internal val handler: GPUTexture) : AutoCloseable {
	override fun close() {
		// nothing to do
	}

	actual fun createView(descriptor: TextureViewDescriptor?): TextureView {
		return TextureView(
			when (descriptor) {
				null -> handler.createView()
				else -> handler.createView(descriptor.convert())
			}
		)
	}
}

private fun TextureViewDescriptor?.convert(): GPUTextureViewDescriptor {
	//TODO
	return object : GPUTextureViewDescriptor {}
}
