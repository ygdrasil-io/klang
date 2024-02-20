package sdl

import libsdl.SDL_Rect

fun rect(x: Int, y: Int, w: Int, h: Int) = SDL_Rect().also {
	it.x = x
	it.y = y
	it.w = w
	it.h = h
}