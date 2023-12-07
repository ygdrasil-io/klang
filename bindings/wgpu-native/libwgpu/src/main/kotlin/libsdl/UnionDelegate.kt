package libsdl

import libsdl.SDL_EventType.*

object SDL_HapticEffectDelegate {
	fun read(union: SDL_HapticEffect) {
		//TODO implement
	}
}

object SDL_WindowShapeParamsDelegate {
	fun read(union: SDL_WindowShapeParams) {
		//TODO implement
	}
}

object SDL_EventDelegate {
	fun read(union: SDL_Event) = with(union) {
		readField("type")
		when (SDL_EventType.of(type)) {
			SDL_QUIT -> setType(SDL_QuitEvent::class.java)
			SDL_APP_TERMINATING, SDL_APP_LOWMEMORY, SDL_APP_WILLENTERBACKGROUND, SDL_APP_DIDENTERBACKGROUND, SDL_APP_WILLENTERFOREGROUND, SDL_APP_DIDENTERFOREGROUND -> setType(
				SDL_OSEvent::class.java
			)

			SDL_LOCALECHANGED -> setType(SDL_CommonEvent::class.java)
			SDL_DISPLAYEVENT -> setType(SDL_DisplayEvent::class.java)
			SDL_WINDOWEVENT -> setType(SDL_WindowEvent::class.java)
			SDL_SYSWMEVENT -> setType(SDL_SysWMEvent::class.java)
			SDL_KEYDOWN, SDL_KEYUP -> setType(SDL_KeyboardEvent::class.java)
			SDL_TEXTEDITING -> setType(SDL_TextEditingEvent::class.java)
			SDL_TEXTINPUT -> setType(SDL_TextInputEvent::class.java)
			SDL_KEYMAPCHANGED -> setType(SDL_CommonEvent::class.java)
			SDL_TEXTEDITING_EXT -> setType(SDL_TextEditingExtEvent::class.java)
			SDL_MOUSEMOTION -> setType(SDL_MouseMotionEvent::class.java)
			SDL_MOUSEBUTTONDOWN, SDL_MOUSEBUTTONUP -> setType(SDL_MouseButtonEvent::class.java)
			SDL_MOUSEWHEEL -> setType(SDL_MouseWheelEvent::class.java)
			SDL_JOYAXISMOTION -> setType(SDL_JoyAxisEvent::class.java)
			SDL_JOYBALLMOTION -> setType(SDL_JoyBallEvent::class.java)
			SDL_JOYHATMOTION -> setType(SDL_JoyHatEvent::class.java)
			SDL_JOYBUTTONDOWN, SDL_JOYBUTTONUP -> setType(SDL_JoyButtonEvent::class.java)
			SDL_JOYDEVICEADDED, SDL_JOYDEVICEREMOVED -> setType(SDL_JoyDeviceEvent::class.java)
			SDL_JOYBATTERYUPDATED -> setType(SDL_JoyBatteryEvent::class.java)
			SDL_CONTROLLERAXISMOTION -> setType(SDL_ControllerAxisEvent::class.java)
			SDL_CONTROLLERBUTTONDOWN, SDL_CONTROLLERBUTTONUP -> setType(SDL_ControllerButtonEvent::class.java)
			SDL_CONTROLLERDEVICEADDED, SDL_CONTROLLERDEVICEREMOVED, SDL_CONTROLLERDEVICEREMAPPED -> setType(
				SDL_ControllerDeviceEvent::class.java
			)

			SDL_CONTROLLERTOUCHPADDOWN, SDL_CONTROLLERTOUCHPADMOTION, SDL_CONTROLLERTOUCHPADUP -> setType(
				SDL_ControllerTouchpadEvent::class.java
			)

			SDL_CONTROLLERSENSORUPDATE -> setType(SDL_ControllerSensorEvent::class.java)
			SDL_FINGERDOWN, SDL_FINGERUP, SDL_FINGERMOTION -> setType(SDL_TouchFingerEvent::class.java)
			SDL_DOLLARGESTURE, SDL_DOLLARRECORD -> setType(SDL_DollarGestureEvent::class.java)
			SDL_MULTIGESTURE -> setType(SDL_MultiGestureEvent::class.java)
			SDL_CLIPBOARDUPDATE -> setType(SDL_CommonEvent::class.java)
			SDL_DROPFILE, SDL_DROPTEXT, SDL_DROPBEGIN, SDL_DROPCOMPLETE -> setType(SDL_DropEvent::class.java)
			SDL_AUDIODEVICEADDED, SDL_AUDIODEVICEREMOVED -> setType(SDL_AudioDeviceEvent::class.java)
			SDL_SENSORUPDATE -> setType(SDL_SensorEvent::class.java)
			SDL_RENDER_TARGETS_RESET, SDL_RENDER_DEVICE_RESET -> setType(SDL_CommonEvent::class.java)
			else -> if (type >= SDL_USEREVENT.value && type < SDL_LASTEVENT.value) {
				setType(SDL_UserEvent::class.java)
			} else {
				setType(SDL_CommonEvent::class.java)
			}
		}
	}
}