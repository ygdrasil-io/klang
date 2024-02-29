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
		when (WGPUSurfaceGetCurrentTextureStatus.of(surface_texture.status)) {
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
		assert(surface_texture.texture);


		val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")
		val encoder = wgpuDeviceCreateCommandEncoder(device, null)
		val pass = wgpuCommandEncoderBeginRenderPass(encoder,
			WGPURenderPassDescriptor().apply {
				colorAttachmentCount = 1
				colorAttachments = WGPURenderPassColorAttachment().apply {
					//view = TODO()
					loadOp = WGPULoadOp.WGPULoadOp_Clear
					storeOp = WGPUStoreOp.WGPUStoreOp_Store
					clearValue = WGPUColor().apply {
						r = 0.0
						g = 0.0
						b = 0.4
						a = 1.0
					}
				}.pointer
			}
		)
		wgpuRenderPassEncoderEnd(pass)

		wgpuQueueSubmit(queue, NativeLong(1), pass)


		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}
}