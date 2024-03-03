package io.ygdrasil.wgpu.examples

import com.sun.jna.ptr.IntByReference
import io.ygdrasil.libsdl.*
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.examples.io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.internal.jvm.wgpuGetVersion
import kotlinx.coroutines.runBlocking

fun main() {

	printVersion()
	initSDL()

	runBlocking {
		loop()
	}
}

fun initSDL() {
	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}
}

fun printVersion() {
	println("WGPU version ${wgpuGetVersion()}")
	SDL_version().apply {
		SDL_GetVersion(this)
		println("SDL version $major.$minor.$patch")
	}
}

suspend fun loop() {

	val instance = WGPU.createInstance() ?: error("fail to wgpu instance")

	val window = SDL_CreateWindow(
		"", SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(), 800, 600,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.value
	) ?: error("fail to create window ${SDL_GetError()}")

	val surface = instance.getSurface(window) ?: error("fail to create surface")
	val renderingContext = RenderingContext(surface)

	val adapter = instance.requestAdapter(renderingContext)
		?: error("fail to get adapter")

	val device = adapter.requestDevice()
		?: error("fail to get device")

	renderingContext.configure(device, adapter) {
		val width = IntByReference()
		val height = IntByReference()
		SDL_GetWindowSize(window, width.pointer, height.pointer)
		width.value to height.value
	}

	while (true) {
		blueScreen(device, renderingContext)

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {
		}
	}

	renderingContext.close()
	device.close()
	adapter.close()
	instance.close()
}
