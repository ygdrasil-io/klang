package io.ygdrasil.triangle

import com.sun.jna.NativeLong
import com.sun.jna.ptr.IntByReference
import io.ygdrasil.libsdl.SDL_Event
import io.ygdrasil.libsdl.SDL_GetWindowSize
import io.ygdrasil.libsdl.SDL_PollEvent
import io.ygdrasil.libsdl.SDL_Window
import libwgpu.*

fun step1(
	device: WGPUDevice,
	adapter: WGPUAdapterImpl,
	surface: WGPUSurface,
	window: SDL_Window,
	config: WGPUSurfaceConfiguration
) {

	while (true) {

		val surface_texture = WGPUSurfaceTexture()
		wgpuSurfaceGetCurrentTexture(surface, surface_texture);
		when (WGPUSurfaceGetCurrentTextureStatus.of(surface_texture.status) ?: error("surface status not found")) {
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_Success -> Unit // All good, could check for `surface_texture.suboptimal` here.
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_Timeout,
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_Outdated,
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_Lost -> {
				// Skip this frame, and re-configure surface.
				if (surface_texture.texture != null) {
					wgpuTextureRelease(surface_texture.texture);
				}
				val width = IntByReference()
				val height = IntByReference()
				SDL_GetWindowSize(window, width.pointer, height.pointer)
				config.width = width.value
				config.height = height.value
				wgpuSurfaceConfigure(surface, config)
				continue;
			}

			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_OutOfMemory,
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_DeviceLost,
			WGPUSurfaceGetCurrentTextureStatus.WGPUSurfaceGetCurrentTextureStatus_Force32 -> {
				// Fatal error
				println(LOG_PREFIX + " get_current_texture status=%#.8x\n".format(surface_texture.status))
				return;
			}

		}
		val frame = wgpuTextureCreateView(surface_texture.texture, null) ?: error("fail to get frame")

		val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")
		val encoder = wgpuDeviceCreateCommandEncoder(device, WGPUCommandEncoderDescriptor().apply {
			label = "WGPUCommandEncoderDescriptorKt"
		})

		val render_pass_encoder = wgpuCommandEncoderBeginRenderPass(encoder,
			WGPURenderPassDescriptor().apply {
				label = "WGPURenderPassDescriptorKt"
				colorAttachmentCount = 1L
				colorAttachments = arrayOf(WGPURenderPassColorAttachment.ByReference().apply {
					view = frame
					loadOp = WGPULoadOp.WGPULoadOp_Clear.value
					storeOp = WGPUStoreOp.WGPUStoreOp_Store.value
					clearValue = WGPUColor().apply {
						r = 0.0
						g = 0.0
						b = 0.4
						a = 1.0
					}
				})
			}
		)
		wgpuRenderPassEncoderEnd(render_pass_encoder)

		val commandBuffer = wgpuCommandEncoderFinish(encoder, WGPUCommandBufferDescriptor().apply {
			label = "WGPUCommandBufferDescriptorKt"
		}) ?: error("fail to get commandBuffer")
		wgpuQueueSubmit(queue, NativeLong(1), arrayOf(commandBuffer))

		wgpuSurfacePresent(surface);

		wgpuCommandBufferRelease(commandBuffer);
		wgpuRenderPassEncoderRelease(render_pass_encoder);
		wgpuCommandEncoderRelease(encoder);
		wgpuTextureViewRelease(frame);
		wgpuTextureRelease(surface_texture.texture);


		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}
}