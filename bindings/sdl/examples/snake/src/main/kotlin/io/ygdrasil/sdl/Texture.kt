package io.ygdrasil.sdl

import com.sun.jna.Native
import io.ygdrasil.libsdl.*
import java.io.InputStream
import java.nio.ByteBuffer

internal fun SDL_Renderer.loadTexture(fileName: String): SDL_Texture {
	println("load texture $fileName")
	val resourceStream = findClassPathResource(fileName) ?: error("fail to find resource with name filename")
	val (buffer, bufferSize) = resourceStream.toBuffer()
	val bufferPointer = Native.getDirectBufferPointer(buffer)
	val res = SDL_RWFromMem(bufferPointer, bufferSize)
	val image = SDL_LoadBMP_RW(res, 1)
	val texture = SDL_CreateTextureFromSurface(this, image) ?: error("fail to create texture from filename $fileName")
	SDL_FreeSurface(image)
	return texture
}

private fun InputStream.toBuffer(): Pair<ByteBuffer, Int> {
	val resourceBytes = readAllBytes()
	val buffer = ByteBuffer.allocateDirect(resourceBytes.size)
	buffer.put(resourceBytes)
	val bufferSize = resourceBytes.size
	return Pair(buffer, bufferSize)
}

private fun findClassPathResource(fileName: String): InputStream? =
	AppContext::class.java.classLoader.getResourceAsStream(fileName)