package io.ygdrasil.sdl

import io.ygdrasil.libsdl.*

interface AppContext {
	val window: SDL_Window
	val glContext: SDL_GLContext?
	val renderer: SDL_Renderer
	val textures: MutableList<SDL_Texture>
	val controllers: List<SDL_GameController>

	fun addTexture(filename: String): SDL_Texture
	fun removeTexture(texture: SDL_Texture)
}

