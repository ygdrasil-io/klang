package io.ygdrasil.sdl

import com.sun.jna.Library
import io.ygdrasil.libsdl.*

private class App : AutoCloseable, AppContext {

	override val window: SDL_Window = createWindow()
	override val renderer: SDL_Renderer = createRenderer()
	override val textures = mutableListOf<SDL_Texture>()
	override val controllers: List<SDL_GameController> = findControllers()

	override fun addTexture(filename: String) = renderer.loadTexture(filename)
		.also(textures::add)

	override fun removeTexture(texture: SDL_Texture) {
		if (textures.remove(texture)) {
			SDL_DestroyTexture(texture)
		}
	}

	override fun close() {
		textures.forEach(::SDL_DestroyTexture)
		controllers.forEach(::SDL_GameControllerClose)
		SDL_DestroyRenderer(renderer)
		SDL_DestroyWindow(window)
		SDL_Quit()
	}

	private fun createWindow() = SDL_CreateWindow(
			"", SDL_WINDOWPOS_CENTERED.toInt(),
			SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
			SDL_WindowFlags.SDL_WINDOW_SHOWN.value
		) ?: error("fail to create window ${SDL_GetError()}")

	private fun createRenderer() = SDL_CreateRenderer(
		window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
	) ?: error("fail to create renderer")

	private fun findControllers() = (0 until SDL_NumJoysticks())
		.mapNotNull { index -> SDL_GameControllerOpen(index).also { if (it == null) println("fail to get controller at index $index") } }
}

interface GLESv2: Library { }
interface EGL: Library { }

fun app(
	block: AppContext.() -> Unit
) {

	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}

	App().use(block)
}