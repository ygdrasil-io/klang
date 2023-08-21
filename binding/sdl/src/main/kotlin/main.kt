import com.sun.jna.Pointer
import libsdl.libSDL2Library

typealias unnamed = Pointer

const val SDL_INIT_TIMER: UInt = 0x00000001u
const val SDL_INIT_AUDIO: UInt = 0x00000010u
const val SDL_INIT_VIDEO: UInt = 0x00000020u  // SDL_INIT_VIDEO implies SDL_INIT_EVENTS
const val SDL_INIT_JOYSTICK: UInt = 0x00000200u  // SDL_INIT_JOYSTICK implies SDL_INIT_EVENTS
const val SDL_INIT_HAPTIC: UInt = 0x00001000u
const val SDL_INIT_GAMECONTROLLER: UInt = 0x00002000u  // SDL_INIT_GAMECONTROLLER implies SDL_INIT_JOYSTICK
const val SDL_INIT_EVENTS: UInt = 0x00004000u
const val SDL_INIT_SENSOR: UInt = 0x00008000u
const val SDL_INIT_NOPARACHUTE: UInt = 0x00100000u  // compatibility; this flag is ignored.

val SDL_INIT_EVERYTHING: UInt = SDL_INIT_TIMER or SDL_INIT_AUDIO or SDL_INIT_VIDEO or SDL_INIT_EVENTS or SDL_INIT_JOYSTICK or SDL_INIT_HAPTIC or SDL_INIT_GAMECONTROLLER or SDL_INIT_SENSOR

const val SDL_WINDOWPOS_CENTERED_MASK: UInt = 0x2FFF0000u

fun SDL_WINDOWPOS_CENTERED_DISPLAY(X: Int): Int {
	return SDL_WINDOWPOS_CENTERED_MASK.toInt() or X
}

val SDL_WINDOWPOS_CENTERED: Int = SDL_WINDOWPOS_CENTERED_DISPLAY(0)

fun main() {
	libSDL2Library.SDL_Init(0)

	if (libSDL2Library.SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		println("error initializing SDL: ${libSDL2Library.SDL_GetError()}" );
	}
	val win = libSDL2Library.SDL_CreateWindow(
		Pointer.NULL,
		SDL_WINDOWPOS_CENTERED,
		SDL_WINDOWPOS_CENTERED,
		1000, 1000, 0);
	do {


	} while (true)

}