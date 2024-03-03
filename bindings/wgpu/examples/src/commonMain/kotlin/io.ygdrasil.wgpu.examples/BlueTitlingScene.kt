package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.RenderPassDescriptor

class BlueTitlingScene : Application.Scene() {

	private var blue = 0.0
	private var delta = 5.0
	override fun Application.initialiaze() {
		// Nothing to init here
	}

	override fun Application.render() {
		if (blue > 255.0) {
			delta = -5.0
		} else if (blue < 0) {
			delta = 5.0
		}

		blue += delta

		// Clear the canvas with a render pass
		val encoder = device.createCommandEncoder() ?: error("fail to get command encoder")

		val texture = renderingContext.getCurrentTexture() ?: error("fail to get current texture")
		val view = texture.createView()

		val renderPassEncoder = encoder.beginRenderPass(
			RenderPassDescriptor(
				colorAttachments = arrayOf(
					RenderPassDescriptor.ColorAttachment(
						view = view,
						loadOp = "clear",
						clearValue = arrayOf(0, 0, blue / 255.0, 1.0),
						storeOp = "store"
					)
				)
			)
		)
		renderPassEncoder.end()

		val commandBuffer = encoder.finish()

		device.queue.submit(arrayOf(commandBuffer))

		renderingContext.present()

		commandBuffer.close()
		renderPassEncoder.close()
		encoder.close()
		view.close()
		texture.close()
	}

}