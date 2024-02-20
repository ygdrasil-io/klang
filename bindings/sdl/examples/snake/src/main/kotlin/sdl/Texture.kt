package io.ygdrasil.sdl

import io.ygdrasil.libsdl.*
import java.io.File

internal fun SDL_Renderer.loadTexture(fileName: String): SDL_Texture {
	val filePath = fileName.takeIf { File(it).canRead() } ?: error("Can't find image file.")
	val image = SDL_LoadBMP_RW(SDL_RWFromFile(filePath, "rb"), 1)
	val texture = SDL_CreateTextureFromSurface(this, image) ?: error("fail to create texture from filename $fileName")
	SDL_FreeSurface(image)
	return texture
}