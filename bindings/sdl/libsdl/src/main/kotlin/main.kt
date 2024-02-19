import libsdl.SDL_INIT_EVERYTHING
import libsdl.SDL_WINDOWPOS_CENTERED
import libsdl.SDL_WindowFlags
import libsdl.libSDL2Library

fun main() {

	println("Init SDL")

	if (libSDL2Library.SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		println("error initializing SDL: ${libSDL2Library.SDL_GetError()}" );
		return
	}

	println("create window")

	libSDL2Library.SDL_CreateWindow(
		"Game",
		SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(),
		800, 600,
		SDL_WindowFlags.SDL_WINDOW_SHOWN or SDL_WindowFlags.SDL_WINDOW_RESIZABLE
	)
	do {

		// Set to ~60 fps.
		// 1000 ms/ 60 fps = 1/16 s^2/frame
		libSDL2Library.SDL_Delay(16);
	} while (true)

}