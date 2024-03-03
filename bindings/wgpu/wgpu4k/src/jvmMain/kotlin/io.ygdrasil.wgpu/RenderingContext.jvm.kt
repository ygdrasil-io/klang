package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class RenderingContext(internal val handler: WGPUSurface) : AutoCloseable {
	actual fun getCurrentTexture(): Texture {
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

	fun configure(device: Device, adapter: Adapter, sizeProvider: () -> Pair<Int, Int>) =
		sizeProvider().let { (width, height) ->

			val surface_capabilities = WGPUSurfaceCapabilities()
			wgpuSurfaceGetCapabilities(handler, adapter.handler, surface_capabilities)
			val config = WGPUSurfaceConfiguration().also {
				it.device = device.handler ?: error("")
				it.usage = WGPUTextureUsage.WGPUTextureUsage_RenderAttachment.value
				it.format = surface_capabilities.formats?.getInt(0) ?: error("")
				it.presentMode = WGPUPresentMode.WGPUPresentMode_Fifo.value
				it.alphaMode = surface_capabilities.alphaModes?.getInt(0) ?: error("")
				it.width = width
				it.height = height
			}

			wgpuSurfaceConfigure(handler, config)

		}

}