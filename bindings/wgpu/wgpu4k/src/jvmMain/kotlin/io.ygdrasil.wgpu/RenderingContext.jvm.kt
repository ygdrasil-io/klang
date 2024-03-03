package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class RenderingContext(internal val handler: WGPUSurface) : AutoCloseable {
	actual fun getCurrentTexture(): Texture? {
		val surface_texture = WGPUSurfaceTexture()
		wgpuSurfaceGetCurrentTexture(handler, surface_texture)
		return Texture(surface_texture)
	}

	actual fun present() {
		wgpuSurfacePresent(handler)
	}

	override fun close() {
		wgpuSurfaceRelease(handler)
	}

}