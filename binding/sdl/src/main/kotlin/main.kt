import com.sun.jna.Pointer
import libsdl.libSDL2Library

typealias unnamed = Pointer

fun main() {
	libSDL2Library.SDL_Init(0)
}