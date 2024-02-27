package io.ygdrasil.triangle

import com.sun.jna.Pointer
import io.ygdrasil.libsdl.*
import io.ygdrasil.wgpu.SDL_GetWGPUSurface
import libwgpu.*


fun main() {
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
		SDL_WINDOWPOS_CENTERED.toInt(), 100, 100,
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
		override fun invoke(statusAsInt: Int, adapterImpl: WGPUAdapterImpl, message: String, param4: Pointer?) {
			println("WGPURequestAdapterCallback")
			val status = WGPURequestAdapterStatus.of(statusAsInt)
			if (status == WGPURequestAdapterStatus.WGPURequestAdapterStatus_Success) {
				adapter = adapterImpl
			} else {
				println(LOG_PREFIX + " request_adapter status=%.8X message=%s\n".format(status, message))
			}
		}
	}

	wgpuInstanceRequestAdapter(instance, options, handleRequestAdapter, null)
	check(adapter != null) { "fail to get adapter" }

	var device: WGPUDevice? = null
	val handleRequestDevice = object : WGPURequestDeviceCallback {
		override fun invoke(statusAsInt: Int, deviceImpl: WGPUDeviceImpl, message: String, param4: Pointer?) {
			println("WGPURequestDeviceCallback")
			val status = WGPURequestDeviceStatus.of(statusAsInt)
			if (status == WGPURequestDeviceStatus.WGPURequestDeviceStatus_Success) {
				device = deviceImpl
			} else {
				println(LOG_PREFIX + " request_device status=%#.8x message=%s\n".format(status, message));
			}
		}
	}

	wgpuAdapterRequestDevice(adapter, null, handleRequestDevice, null)
	check(device != null) { "fail to get device" }

	val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")

	val shader_module = wgpuDeviceCreateShaderModule(
		device,
		WGPUShaderModuleDescriptor().apply {
			label = "shader.wgsl"
			nextInChain = WGPUShaderModuleWGSLDescriptor().apply {
				chain = WGPUChainedStruct().apply {
					sType = WGPUSType.WGPUSType_ShaderModuleWGSLDescriptor.value
				}
				code = shader
			}
		})


	while (true) {

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}
}