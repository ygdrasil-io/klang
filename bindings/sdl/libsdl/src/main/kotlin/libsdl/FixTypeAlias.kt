package libsdl

import com.sun.jna.Callback
import com.sun.jna.Pointer

typealias SDL_iconv_t = Pointer
typealias SDL_JoystickGUID = Pointer
typealias SDL_AssertionHandler = Callback
typealias SDL_HitTest = Callback
typealias SDL_Sensor = Pointer
typealias SDL_GameController = Pointer
typealias SDL_Joystick = Pointer
typealias SDL_hid_device = Pointer
typealias SDL_Haptic = Pointer
typealias SDL_sem = Pointer
typealias SDL_AudioStream = Pointer
typealias int32_t = Int