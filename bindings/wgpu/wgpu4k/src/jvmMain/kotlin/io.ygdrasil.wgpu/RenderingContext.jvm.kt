package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class RenderingContext(internal val handler: WGPUSurface) : AutoCloseable {

	private val surfaceCapabilities = WGPUSurfaceCapabilities()

	actual val textureFormat: TextureFormat by lazy {
		surfaceCapabilities.formats?.getInt(0)
			?.let { TextureFormat.of(it) ?: error("texture format not found") }
			?: error("call first computeSurfaceCapabilities")
	}

	actual fun getCurrentTexture(): Texture {
		val surfaceTexture = WGPUSurfaceTexture()
		wgpuSurfaceGetCurrentTexture(handler, surfaceTexture)
		return Texture(surfaceTexture)
	}

	actual fun present() {
		wgpuSurfacePresent(handler)
	}

	override fun close() {
		wgpuSurfaceRelease(handler)
	}

	fun computeSurfaceCapabilities(adapter: Adapter) {
		wgpuSurfaceGetCapabilities(handler, adapter.handler, surfaceCapabilities)
	}

	fun configure(device: Device, sizeProvider: () -> Pair<Int, Int>) =
		sizeProvider().let { (width, height) ->

			if (surfaceCapabilities.formats == null) error("call computeSurfaceCapabilities(adapter: Adapter) before configure")

			val config = WGPUSurfaceConfiguration().also {
				it.device = device.handler ?: error("")
				it.usage = WGPUTextureUsage.WGPUTextureUsage_RenderAttachment.value
				it.format = textureFormat.value
				it.presentMode = WGPUPresentMode.WGPUPresentMode_Fifo.value
				it.alphaMode = surfaceCapabilities.alphaModes?.getInt(0) ?: error("")
				it.width = width
				it.height = height
			}

			wgpuSurfaceConfigure(handler, config)

		}

}