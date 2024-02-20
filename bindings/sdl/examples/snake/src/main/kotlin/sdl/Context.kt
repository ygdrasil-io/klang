package sdl

import libsdl.*

interface AppContext {
	val window: SDL_Window
	val renderer: SDL_Renderer
	val textures: MutableList<SDL_Texture>
	val controllers: List<SDL_GameController>

	fun addTexture(filename: String): SDL_Texture
	fun removeTexture(texture: SDL_Texture)
}

class App : AutoCloseable, AppContext {

	override val window: SDL_Window
	override val renderer: SDL_Renderer
	override val textures = mutableListOf<SDL_Texture>()
	override val controllers: List<SDL_GameController>

	init {
		if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
			error("SDL_Init Error: ${SDL_GetError()}")
		}

		window = SDL_CreateWindow(
			"", SDL_WINDOWPOS_CENTERED.toInt(),
			SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
			SDL_WindowFlags.SDL_WINDOW_SHOWN.value
		) ?: error("fail to create window")

		renderer = SDL_CreateRenderer(
			window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
		) ?: error("fail to create renderer")

		controllers = (0 until SDL_NumJoysticks())
			.mapNotNull { index -> SDL_GameControllerOpen(index).also { if(it == null) println("fail to get controller at index $index") } }
	}

	override fun addTexture(filename: String) = renderer.loadTexture(filename)
		.also(textures::add)

	override fun removeTexture(texture: SDL_Texture) {
		if (textures.remove(texture)) {
			SDL_DestroyTexture(texture)
		}
	}

	override fun close() {
		textures.forEach(::SDL_DestroyTexture)
		SDL_DestroyRenderer(renderer)
		SDL_DestroyWindow(window)
		SDL_Quit()
	}
}

fun app(block: App.() -> Unit) {
	App().use(block)
}