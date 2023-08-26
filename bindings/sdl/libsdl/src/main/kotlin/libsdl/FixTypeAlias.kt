package libsdl

import com.sun.jna.Callback
import com.sun.jna.Pointer

typealias SDL_iconv_t = Pointer
typealias SDL_JoystickGUID = Pointer
typealias SDL_AssertionHandler = Callback
typealias SDL_HitTest = Callback