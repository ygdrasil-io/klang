@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUCanvasConfiguration
import io.ygdrasil.wgpu.internal.js.GPUCanvasContext
import io.ygdrasil.wgpu.internal.js.GPUDevice
import org.w3c.dom.HTMLCanvasElement

actual class RenderingContext(private val handler: GPUCanvasContext) : AutoCloseable {
	override fun close() {
		// Nothing to do on js
	}

	actual fun getCurrentTexture(): Texture? {
		return Texture(handler.getCurrentTexture())
	}

	actual fun present() {
		// Nothing to do
	}

	fun configure(canvasConfiguration: CanvasConfiguration) {
		handler.configure(canvasConfiguration.convert())
	}
}

fun HTMLCanvasElement.getRenderingContext() = (getContext("webgpu") as? GPUCanvasContext)?.let { RenderingContext(it) }

data class CanvasConfiguration(
	var device: Device,
	var format: String = navigator.gpu!!.getPreferredCanvasFormat(),
	var usage: GPUTextureUsageFlags? = null,
	var viewFormats: Array<String?>? = null,
	var colorSpace: Any? = null,
	var alphaMode: String? = null
) {

	fun convert(): GPUCanvasConfiguration = object : GPUCanvasConfiguration {
		override var device: GPUDevice = this@CanvasConfiguration.device.handler
		override var format: String = this@CanvasConfiguration.format
		override var usage: GPUTextureUsageFlags? = this@CanvasConfiguration.usage ?: undefined
		override var viewFormats: Array<String?>? = this@CanvasConfiguration.viewFormats ?: undefined
		override var colorSpace: Any? = this@CanvasConfiguration.colorSpace ?: undefined
		override var alphaMode: String? = this@CanvasConfiguration.alphaMode ?: undefined
	}
}