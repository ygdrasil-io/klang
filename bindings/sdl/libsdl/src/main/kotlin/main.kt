import com.sun.jna.Pointer
import libsdl.SDL_WindowFlags
import libsdl.libSDL2Library

typealias unnamed = Pointer

const val SDL_INIT_TIMER: Int = 0x00000001
const val SDL_INIT_AUDIO: Int = 0x00000010
const val SDL_INIT_VIDEO: Int = 0x00000020  // SDL_INIT_VIDEO implies SDL_INIT_EVENTS
const val SDL_INIT_JOYSTICK: Int = 0x00000200  // SDL_INIT_JOYSTICK implies SDL_INIT_EVENTS
const val SDL_INIT_HAPTIC: Int = 0x00001000
const val SDL_INIT_GAMECONTROLLER: Int = 0x00002000  // SDL_INIT_GAMECONTROLLER implies SDL_INIT_JOYSTICK
const val SDL_INIT_EVENTS: Int = 0x00004000
const val SDL_INIT_SENSOR: Int = 0x00008000
const val SDL_INIT_NOPARACHUTE: Int = 0x00100000  // compatibility; this flag is ignored.

val SDL_INIT_EVERYTHING: Int = SDL_INIT_TIMER or SDL_INIT_AUDIO or SDL_INIT_VIDEO or SDL_INIT_EVENTS or SDL_INIT_JOYSTICK or SDL_INIT_HAPTIC or SDL_INIT_GAMECONTROLLER or SDL_INIT_SENSOR

const val SDL_WINDOWPOS_CENTERED_MASK: Int = 0x2FFF0000

fun SDL_WINDOWPOS_CENTERED_DISPLAY(X: Int): Int {
	return SDL_WINDOWPOS_CENTERED_MASK or X
}

val SDL_WINDOWPOS_CENTERED: Int = SDL_WINDOWPOS_CENTERED_DISPLAY(0)

fun main() {
	libSDL2Library.SDL_Init(0)

	if (libSDL2Library.SDL_Init(SDL_INIT_EVERYTHING) != 0) {
		println("error initializing SDL: ${libSDL2Library.SDL_GetError()}" );
		return
	}
	libSDL2Library.SDL_CreateWindow(
		"Game",
		SDL_WINDOWPOS_CENTERED,
		SDL_WINDOWPOS_CENTERED,
		800, 600,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.nativeValue.toInt() or SDL_WindowFlags.SDL_WINDOW_FULLSCREEN_DESKTOP.nativeValue.toInt()
	)
	do {


		// Set to ~60 fps.
		// 1000 ms/ 60 fps = 1/16 s^2/frame
		libSDL2Library.SDL_Delay(16);
	} while (true)

}