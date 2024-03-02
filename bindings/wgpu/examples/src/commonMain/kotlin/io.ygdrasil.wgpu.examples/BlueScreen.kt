package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.Device


private var blue = 0.0
fun blueScreen(device: Device) {
	if (blue >= 255.0) {
		blue = 0.0
	} else {
		blue += 5.0
	}

	// Clear the canvas with a render pass
	val encoder = device.createCommandEncoder() ?: error("fail to get command encoder")
}