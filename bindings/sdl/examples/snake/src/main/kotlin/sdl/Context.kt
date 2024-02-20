package io.ygdrasil.sdl

import io.ygdrasil.libsdl.SDL_GameController
import io.ygdrasil.libsdl.SDL_Renderer
import io.ygdrasil.libsdl.SDL_Texture
import io.ygdrasil.libsdl.SDL_Window

interface AppContext {
	val window: SDL_Window
	val renderer: SDL_Renderer
	val textures: MutableList<SDL_Texture>
	val controllers: List<SDL_GameController>

	fun addTexture(filename: String): SDL_Texture
	fun removeTexture(texture: SDL_Texture)
}

