package io.ygdrasil.sdl

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
	) ?: error("fail to create window")

	private fun createRenderer() = SDL_CreateRenderer(
		window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
	) ?: error("fail to create renderer")

	private fun findControllers() = (0 until SDL_NumJoysticks())
		.mapNotNull { index -> SDL_GameControllerOpen(index).also { if (it == null) println("fail to get controller at index $index") } }
}

fun app(
	useGlES: Boolean = false,
	block: AppContext.() -> Unit
) {

	//SDL_HINT()

	if (useGlES) {
		// IMPORTANT! These sets must go BEFORE SDL_Init
		// Request OpenGL ES 3.0
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_FLAGS.value, SDL_GLprofile.SDL_GL_CONTEXT_PROFILE_ES.value);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MAJOR_VERSION.value, 3);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MINOR_VERSION.value, 0);

		// Want double-buffering
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_DOUBLEBUFFER.value, 1);
	}


	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}

	App().use{

		if (useGlES) {
			val context = SDL_GL_CreateContext(it.window);
		}

		it.block()

	}
}