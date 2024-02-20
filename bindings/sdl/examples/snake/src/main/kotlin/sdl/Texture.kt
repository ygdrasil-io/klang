package sdl

import libsdl.SDL_Renderer
import libsdl.SDL_Texture
import libsdl.libSDL2Library
import java.io.File

internal fun SDL_Renderer.loadTexture(fileName: String): SDL_Texture {
	val filePath = fileName.takeIf { File(it).canRead() } ?: error("Can't find image file.")
	val image = libSDL2Library.SDL_LoadBMP_RW(libSDL2Library.SDL_RWFromFile(filePath, "rb"), 1)
	return libSDL2Library.SDL_CreateTextureFromSurface(this, image)
}