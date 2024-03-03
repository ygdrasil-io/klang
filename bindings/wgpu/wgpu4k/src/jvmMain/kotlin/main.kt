package io.ygdrasil.wgpu.examples

import com.sun.jna.ptr.IntByReference
import io.ygdrasil.libsdl.*
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.RenderPassDescriptor
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.examples.io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.internal.jvm.*
import kotlinx.coroutines.runBlocking

fun main() {
	runBlocking {
		loop()
	}
}

suspend fun loop() {
	println("WGPU version ${wgpuGetVersion()}")
	SDL_version().apply {
		SDL_GetVersion(this)
		println("SDL version $major.$minor.$patch")
	}

	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}

	val instance = WGPU.createInstance() ?: error("fail to wgpu instance")

	val window = SDL_CreateWindow(
		"", SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(), 800, 600,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.value
	) ?: error("fail to create window ${SDL_GetError()}")

	val surface = instance.getSurface(window) ?: error("fail to create surface")

	val options = WGPURequestAdapterOptions().apply {
		compatibleSurface = surface
		powerPreference = WGPUPowerPreference.WGPUPowerPreference_Undefined.value
		backendType = WGPUBackendType.WGPUBackendType_Metal.value
	}

	val adapter = instance.requestAdapter(options)
		?: error("fail to get adapter")

	val device = adapter.requestDevice()
		?: error("fail to get device")

	val width = IntByReference()
	val height = IntByReference()
	SDL_GetWindowSize(window, width.pointer, height.pointer)

	val surface_capabilities = WGPUSurfaceCapabilities()
	wgpuSurfaceGetCapabilities(surface, adapter.handler, surface_capabilities)
	val config = WGPUSurfaceConfiguration().apply {
		this.device = device.handler ?: error("")
		usage = WGPUTextureUsage.WGPUTextureUsage_RenderAttachment.value
		format = surface_capabilities.formats?.getInt(0) ?: error("")
		presentMode = WGPUPresentMode.WGPUPresentMode_Fifo.value
		alphaMode = surface_capabilities.alphaModes?.getInt(0) ?: error("")
		this.width = width.value
		this.height = height.value
	}

	wgpuSurfaceConfigure(surface, config)

	RenderingContext(surface).use { renderingContext ->
		step1(device, renderingContext)
	}

	device.close()
	adapter.close()
	instance.close()
}

fun step1(
	device: Device,
	renderingContext: RenderingContext
) {

	while (true) {

		val texture = renderingContext.getCurrentTexture() ?: error("fail to get texture")
		val view = texture.createView()

		val encoder = device.createCommandEncoder()

		val renderPassEncoder = encoder.beginRenderPass(
			RenderPassDescriptor(
				colorAttachments = arrayOf(
					RenderPassDescriptor.ColorAttachment(
						view = view,
						loadOp = "clear",
						storeOp = "store",
						clearValue = arrayOf(
							0.0,
							0.0,
							0.4,
							1.0
						)
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

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}
}