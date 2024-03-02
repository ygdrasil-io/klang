package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.RenderPassDescriptor
import io.ygdrasil.wgpu.RenderingContext


private var blue = 200.0
fun blueScreen(device: Device, renderingContext: RenderingContext) {
	if (blue >= 255.0) {
		blue = 0.0
	} else {
		blue += 5.0
	}

	// Clear the canvas with a render pass
	val encoder = device.createCommandEncoder() ?: error("fail to get command encoder")

	val texture = renderingContext.getCurrentTexture() ?: error("fail to get current texture")

	val pass = encoder.beginRenderPass(
		RenderPassDescriptor(
			colorAttachments = arrayOf(
				RenderPassDescriptor.ColorAttachment(
					 view = texture.createView(),
					loadOp = "clear",
					clearValue = arrayOf(0, 0, blue / 255.0, 1.0),
					storeOp = "store"
				)
			)
		)
	)
	pass.end()
	device.queue.submit(arrayOf(encoder.finish()))
}