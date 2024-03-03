package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPUSurfaceTexture
import io.ygdrasil.wgpu.internal.jvm.WGPUTextureViewDescriptor
import io.ygdrasil.wgpu.internal.jvm.wgpuTextureCreateView
import io.ygdrasil.wgpu.internal.jvm.wgpuTextureRelease


actual class Texture(private val handler: WGPUSurfaceTexture) : AutoCloseable {
	actual fun createView(descriptor: TextureViewDescriptor?): TextureView {
		return TextureView(
			wgpuTextureCreateView(handler.texture, descriptor?.convert())
				?: error("fail to create texture view")
		)
	}

	override fun close() {
		wgpuTextureRelease(handler.texture)
	}
}

private fun TextureViewDescriptor?.convert(): WGPUTextureViewDescriptor? = WGPUTextureViewDescriptor().also {
	// TODO
}