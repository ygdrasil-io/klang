package io.ygdrasil.wgpu.examples

import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import io.ygdrasil.libsdl.*
import io.ygdrasil.wgpu.CommandEncoder
import io.ygdrasil.wgpu.RenderPassDescriptor
import io.ygdrasil.wgpu.RenderingContext
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

	val instance = wgpuCreateInstance(null) ?: error("fail to wgpu instance")

	val window = SDL_CreateWindow(
		"", SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(), 800, 600,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.value
	) ?: error("fail to create window ${SDL_GetError()}")

	val surface = SDL_GetWGPUSurface(instance, window)
	check(surface != null) { "fail to create surface" }

	val options = WGPURequestAdapterOptions().apply {
		compatibleSurface = surface
		powerPreference = WGPUPowerPreference.WGPUPowerPreference_Undefined.value
		backendType = WGPUBackendType.WGPUBackendType_Metal.value
	}

	var adapter: WGPUAdapterImpl? = null
	val handleRequestAdapter = object : WGPURequestAdapterCallback {
		override fun invoke(statusAsInt: Int, adapterImpl: WGPUAdapterImpl, message: String?, param4: Pointer?) {
			println("WGPURequestAdapterCallback")
			val status = WGPURequestAdapterStatus.of(statusAsInt)
			if (status == WGPURequestAdapterStatus.WGPURequestAdapterStatus_Success) {
				adapter = adapterImpl
			} else {
				println("request_adapter status=%.8X message=%s\n".format(status, message))
			}
		}
	}

	wgpuInstanceRequestAdapter(instance, options, handleRequestAdapter, null)
	check(adapter != null) { "fail to get adapter" }

	var device: WGPUDevice? = null
	val handleRequestDevice = object : WGPURequestDeviceCallback {
		override fun invoke(statusAsInt: Int, deviceImpl: WGPUDeviceImpl, message: String?, param4: Pointer?) {
			println("WGPURequestDeviceCallback")
			val status = WGPURequestDeviceStatus.of(statusAsInt)
			if (status == WGPURequestDeviceStatus.WGPURequestDeviceStatus_Success) {
				device = deviceImpl
			} else {
				println(" request_device status=%#.8x message=%s\n".format(status, message))
			}
		}
	}

	wgpuAdapterRequestDevice(adapter, null, handleRequestDevice, null)
	check(device != null) { "fail to get device" }

	val width = IntByReference()
	val height = IntByReference()
	SDL_GetWindowSize(window, width.pointer, height.pointer)

	val surface_capabilities = WGPUSurfaceCapabilities()
	wgpuSurfaceGetCapabilities(surface, adapter, surface_capabilities)
	val config = WGPUSurfaceConfiguration().apply {
		this.device = device ?: error("")
		usage = WGPUTextureUsage.WGPUTextureUsage_RenderAttachment.value
		format = surface_capabilities.formats?.getInt(0) ?: error("")
		presentMode = WGPUPresentMode.WGPUPresentMode_Fifo.value
		alphaMode = surface_capabilities.alphaModes?.getInt(0) ?: error("")
		this.width = width.value
		this.height = height.value
	}

	wgpuSurfaceConfigure(surface, config)

	RenderingContext(surface).use { renderingContext ->
		step1(device!!, renderingContext)
	}


	wgpuAdapterRelease(adapter)
	wgpuInstanceRelease(instance)
}

fun step1(
	device: WGPUDevice,
	renderingContext: RenderingContext
) {

	while (true) {

		val texture = renderingContext.getCurrentTexture() ?: error("fail to get texture")
		val view = texture.createView()

		val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")

		val encoder = CommandEncoder(wgpuDeviceCreateCommandEncoder(device, WGPUCommandEncoderDescriptor().apply {
			label = "WGPUCommandEncoderDescriptorKt"
		}) ?: error(""))

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

		wgpuQueueSubmit(queue, NativeLong(1), arrayOf(commandBuffer.handler))

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