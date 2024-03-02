package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.internal.js.*
import io.ygdrasil.wgpu.navigator
import io.ygdrasil.wgpu.requestAdapter
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

class CanvasConfiguration(override var device: GPUDevice, override var format: String) : GPUCanvasConfiguration

val UPDATE_INTERVAL = (1000.0 / 60.0).toInt() // 60 Frame per second
private var blue = 0.0

fun blueCanvas() = GlobalScope.launch {
	val canvas = (document.getElementById("webgpu") as? HTMLCanvasElement) ?: error("fail to get canvas")

	val adapter = requestAdapter() ?: error("No appropriate Adapter found.")

	val device = adapter.requestDevice() ?: error("No appropriate Device found.")

	// Canvas configuration
	val context = (canvas.getContext("webgpu") as? GPUCanvasContext)
		?: error("fail to get context")
	val canvasFormat = navigator.gpu!!.getPreferredCanvasFormat()
	context.configure(
		CanvasConfiguration(
			device = device.handler,
			format = canvasFormat
		).apply {

		}
	)

	println("UPDATE_INTERVAL $UPDATE_INTERVAL")

	// Schedule render() to run repeatedly
	setInterval({
		blueScreen(device)
		render(device, context)
	}, UPDATE_INTERVAL);

}

fun render(device: Device, context: GPUCanvasContext) {
	if (blue >= 255.0) {
		blue = 0.0
	} else {
		blue += 5.0
	}

	// Clear the canvas with a render pass
	val encoder = device.createCommandEncoder() ?: error("fail to get command encoder")

	val texture = context.getCurrentTexture()

	val colorAttachment = object : GPURenderPassColorAttachment {
		override var view = texture.createView()
		override var loadOp = "clear"
		override var clearValue: Array<Number>? = arrayOf(0, 0, blue / 255.0, 1.0)
		override var storeOp = "store"
	}

	val renderPassDescriptor = object : GPURenderPassDescriptor {
		override var colorAttachments: Array<GPURenderPassColorAttachment> = arrayOf(colorAttachment)
	}

	val pass = encoder.beginRenderPass(renderPassDescriptor)
	pass.end()
	device.queue.submit(arrayOf(encoder.finish()))
	texture.destroy()

}

external fun setInterval(render: () -> Unit, updateInterval: Int)

