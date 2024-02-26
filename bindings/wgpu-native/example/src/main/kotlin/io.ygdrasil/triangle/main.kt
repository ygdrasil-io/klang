package io.ygdrasil.triangle

import io.ygdrasil.libsdl.*
import libwgpu.*


fun main() {

	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}
	frmwrk_setup_logging(WGPULogLevel.WGPULogLevel_Warn)
	val instance = wgpuCreateInstance(null);

	val window = SDL_CreateWindow(
		"", SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(), 100, 100,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.value
	) ?: error("fail to create window ${SDL_GetError()}")

	val renderer = SDL_CreateRenderer(
		window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
	) ?: error("fail to create renderer")

	val metalLayer = SDL_RenderGetMetalLayer(renderer) ?: error("fail to get metal layer")

	val surfaceDescriptor = WGPUSurfaceDescriptor().apply {
		nextInChain =  WGPUSurfaceDescriptorFromMetalLayer().apply {
			chain = WGPUChainedStruct().apply {
				sType = WGPUSType.WGPUSType_SurfaceDescriptorFromMetalLayer.value
			}
			layer = metalLayer
		}
	}

	val surface = wgpuInstanceCreateSurface(instance, surfaceDescriptor)
	check(surface != null) { "fail to create surface" }

	val options = WGPURequestAdapterOptions().apply {
		compatibleSurface = surface
	}

	var adapter: WGPUAdapterImpl? = null

	val handleRequestAdapter = handleRequestAdapter() {
		adapter = it
	}

	wgpuInstanceRequestAdapter(instance, options, handleRequestAdapter, null);
	check(adapter != null) { "fail to get adapter" }

	while (true) {

		SDL_RenderClear(renderer)
		SDL_SetRenderDrawColor(
			renderer,
			(200 / 2).toByte(),
			(230 / 2).toByte(),
			(151 / 2).toByte(),
			SDL_ALPHA_OPAQUE.toByte()
		)

		SDL_RenderPresent(renderer)

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}
}