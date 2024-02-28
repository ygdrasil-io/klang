package io.ygdrasil.triangle

import com.sun.jna.NativeLong
import libwgpu.*

fun step1(device: WGPUDevice) {

	val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")
	val encoder = wgpuDeviceCreateCommandEncoder(device, null)
	val pass = wgpuCommandEncoderBeginRenderPass(encoder,
		WGPURenderPassDescriptor().apply {
			colorAttachmentCount = 1
			colorAttachments.apply {
				//view = TODO()
				loadOp = WGPULoadOp.WGPULoadOp_Clear
				storeOp = WGPUStoreOp.WGPUStoreOp_Store
				clearValue = WGPUColor().apply {
					r = 0
					g = 0
					b = 0.4
					a = 1.0
				}
			}
		}
	)
	wgpuRenderPassEncoderEnd(pass)

	wgpuQueueSubmit(queue, NativeLong(1), pass)
}