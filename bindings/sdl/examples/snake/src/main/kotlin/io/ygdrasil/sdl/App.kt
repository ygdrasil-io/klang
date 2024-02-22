package io.ygdrasil.sdl

import com.sun.jna.Library
import com.sun.jna.Native
import io.ygdrasil.libsdl.*

private class App(useGlES: Boolean) : AutoCloseable, AppContext {

	override val window: SDL_Window
	override val glContext: SDL_GLContext?
	override val renderer: SDL_Renderer
	override val textures = mutableListOf<SDL_Texture>()
	override val controllers: List<SDL_GameController> = findControllers()

	init {

		window = createWindow(useGlES)

		glContext = SDL_GL_CreateContext(window) ?: error("fail to create GLContext ${SDL_GetError()}")
		renderer = createRenderer()
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
		controllers.forEach(::SDL_GameControllerClose)
		SDL_DestroyRenderer(renderer)
		SDL_DestroyWindow(window)
		SDL_Quit()
	}

	private fun createWindow(useGlES: Boolean) = when {
		useGlES -> SDL_CreateWindow(
			"", SDL_WINDOWPOS_CENTERED.toInt(),
			SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
			SDL_WindowFlags.SDL_WINDOW_SHOWN or SDL_WindowFlags.SDL_WINDOW_OPENGL
		)
		else -> SDL_CreateWindow(
			"", SDL_WINDOWPOS_CENTERED.toInt(),
			SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
			SDL_WindowFlags.SDL_WINDOW_SHOWN.value
		)
	} ?: error("fail to create window ${SDL_GetError()}")

	private fun createRenderer() = SDL_CreateRenderer(
		window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
	) ?: error("fail to create renderer")

	private fun findControllers() = (0 until SDL_NumJoysticks())
		.mapNotNull { index -> SDL_GameControllerOpen(index).also { if (it == null) println("fail to get controller at index $index") } }
}

interface GLESv2: Library { }
interface EGL: Library { }

fun app(
	useGlES: Boolean = false,
	block: AppContext.() -> Unit
) {

	if (useGlES) {

		Native.load("GLESv2", GLESv2::class.java, mapOf<String, String>())
		Native.load("EGL", EGL::class.java, mapOf<String, String>())

		// Request OpenGL ES 3.0
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_FLAGS.value, SDL_GLprofile.SDL_GL_CONTEXT_PROFILE_ES.value);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MAJOR_VERSION.value, 3);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MINOR_VERSION.value, 0);

		// Want double-buffering
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_DOUBLEBUFFER.value, 1);
		/*SDL_SetHint(SDL_HINT_OPENGL_ES_DRIVER, "1")
		SDL_SetHint(SDL_HINT_RENDER_DRIVER, "opengles");

		// IMPORTANT! These sets must go BEFORE SDL_Init
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_PROFILE_MASK.value, SDL_GLprofile.SDL_GL_CONTEXT_PROFILE_ES.value);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_EGL.value, 1);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_FLAGS.value, 0);
		// Request OpenGL ES 3.0
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MAJOR_VERSION.value, 3);
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_CONTEXT_MINOR_VERSION.value, 0);

		// Want double-buffering
		SDL_GL_SetAttribute(SDL_GLattr.SDL_GL_DOUBLEBUFFER.value, 1);*/


	}

	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}


	App(useGlES).use{

		it.block()

	}
}