package sdl

import libsdl.*

interface AppContext {
	val window: SDL_Window
	val renderer: SDL_Renderer
	val textures: MutableList<SDL_Texture>

	fun addTexture(filename: String): SDL_Texture
	fun removeTexture(texture: SDL_Texture)
}

class App : AutoCloseable, AppContext {

	override val window: SDL_Window
	override val renderer: SDL_Renderer
	override val textures = mutableListOf<SDL_Texture>()

	init {
		if (libSDL2Library.SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
			error("SDL_Init Error: ${libSDL2Library.SDL_GetError()}")
		}

		window = libSDL2Library.SDL_CreateWindow(
			"", SDL_WINDOWPOS_CENTERED.toInt(),
			SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
			SDL_WindowFlags.SDL_WINDOW_SHOWN.value
		)

		renderer = libSDL2Library.SDL_CreateRenderer(
			window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
		)

	}

	override fun addTexture(filename: String) = renderer.loadTexture(filename)
		.also(textures::add)

	override fun removeTexture(texture: SDL_Texture) {
		if (textures.remove(texture)) {
			libSDL2Library.SDL_DestroyTexture(texture)
		}
	}

	override fun close() {
		textures.forEach(libSDL2Library::SDL_DestroyTexture)
		libSDL2Library.SDL_DestroyRenderer(renderer)
		libSDL2Library.SDL_DestroyWindow(window)
		libSDL2Library.SDL_Quit()
	}
}