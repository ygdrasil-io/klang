package snake

import com.sun.jna.Pointer
import com.sun.jna.Union
import io.github.libsdl4j.api.event.SDL_EventType
import io.github.libsdl4j.api.event.events.*

/**
 * General event class.
 *
 *
 * This class is a union of all event info objects from SDL.
 * You can pass an empty instance to methods such as [SdlEvents.SDL_PollEvent]
 * and have it populated with event info.
 *
 *
 * Although it is as close a mapping of a C union as possible, Java does not really support that kind of thing.
 * Therefore, unlike in C where you can read from any union member and use it as a view to the raw data,
 * in JNA union, it must be selected which union member is the active field.
 * When the object is populated in a native method, it is done automatically (by [.read])
 * If you populate the object in Java, always remember to select the active member field
 * using [.setType].
 */
class SDL_Event : Union {
    /** Event type, shared with all events  */
	@JvmField
    var type = 0

    /** Common event data  */
	@JvmField
    var common: SDL_CommonEvent? = null

    /** Display event data  */
	@JvmField
    var display: SDL_DisplayEvent? = null

    /** Window event data  */
	@JvmField
    var window: SDL_WindowEvent? = null

    /** Keyboard event data  */
	@JvmField
    var key: SDL_KeyboardEvent? = null

    /** Text editing event data  */
	@JvmField
    var edit: SDL_TextEditingEvent? = null

    /** Text input event data  */
	@JvmField
    var text: SDL_TextInputEvent? = null

    /** Extended text editing event data  */
	@JvmField
    var editExt: SDL_TextEditingExtEvent? = null

    /** Mouse motion event data  */
	@JvmField
    var motion: SDL_MouseMotionEvent? = null

    /** Mouse button event data  */
	@JvmField
    var button: SDL_MouseButtonEvent? = null

    /** Mouse wheel event data  */
	@JvmField
    var wheel: SDL_MouseWheelEvent? = null

    /** Joystick axis event data  */
	@JvmField
    var jaxis: SDL_JoyAxisEvent? = null

    /** Joystick ball event data  */
	@JvmField
    var jball: SDL_JoyBallEvent? = null

    /** Joystick hat event data  */
	@JvmField
    var jhat: SDL_JoyHatEvent? = null

    /** Joystick button event data  */
	@JvmField
    var jbutton: SDL_JoyButtonEvent? = null

    /** Joystick device change event data  */
	@JvmField
    var jdevice: SDL_JoyDeviceEvent? = null

    /** Joystick battery event data  */
	@JvmField
    var jbattery: SDL_JoyBatteryEvent? = null

    /** Game Controller axis event data  */
	@JvmField
    var caxis: SDL_ControllerAxisEvent? = null

    /** Game Controller button event data  */
	@JvmField
    var cbutton: SDL_ControllerButtonEvent? = null

    /** Game Controller device event data  */
	@JvmField
    var cdevice: SDL_ControllerDeviceEvent? = null

    /** Game Controller touchpad event data  */
	@JvmField
    var ctouchpad: SDL_ControllerTouchpadEvent? = null

    /** Game Controller sensor event data  */
	@JvmField
    var csensor: SDL_ControllerSensorEvent? = null

    /** Audio device event data  */
	@JvmField
    var adevice: SDL_AudioDeviceEvent? = null

    /** Touch finger event data  */
	@JvmField
    var tfinger: SDL_TouchFingerEvent? = null

    /** Gesture event data  */
	@JvmField
    var mgesture: SDL_MultiGestureEvent? = null

    /** Gesture event data  */
	@JvmField
    var dgesture: SDL_DollarGestureEvent? = null

    /** Drag and drop event data  */
	@JvmField
    var drop: SDL_DropEvent? = null

    /** Sensor event data  */
	@JvmField
    var sensor: SDL_SensorEvent? = null

    /** Quit request event data  */
	@JvmField
    var quit: SDL_QuitEvent? = null
	@JvmField
    var os: SDL_OSEvent? = null

    /** Custom event data  */
	@JvmField
    var user: SDL_UserEvent? = null

    /** System dependent window event data  */
	@JvmField
    var syswm: SDL_SysWMEvent? = null

    constructor()
    constructor(p: Pointer?) : super(p)

    override fun read() {
        readField("type")
        when (type) {
            SDL_EventType.SDL_QUIT -> setType(SDL_QuitEvent::class.java)
            SDL_EventType.SDL_APP_TERMINATING, SDL_EventType.SDL_APP_LOWMEMORY, SDL_EventType.SDL_APP_WILLENTERBACKGROUND, SDL_EventType.SDL_APP_DIDENTERBACKGROUND, SDL_EventType.SDL_APP_WILLENTERFOREGROUND, SDL_EventType.SDL_APP_DIDENTERFOREGROUND -> setType(
                SDL_OSEvent::class.java
            )

            SDL_EventType.SDL_LOCALECHANGED -> setType(SDL_CommonEvent::class.java)
            SDL_EventType.SDL_DISPLAYEVENT -> setType(SDL_DisplayEvent::class.java)
            SDL_EventType.SDL_WINDOWEVENT -> setType(SDL_WindowEvent::class.java)
            SDL_EventType.SDL_SYSWMEVENT -> setType(SDL_SysWMEvent::class.java)
            SDL_EventType.SDL_KEYDOWN, SDL_EventType.SDL_KEYUP -> setType(
                SDL_KeyboardEvent::class.java
            )

            SDL_EventType.SDL_TEXTEDITING -> setType(SDL_TextEditingEvent::class.java)
            SDL_EventType.SDL_TEXTINPUT -> setType(SDL_TextInputEvent::class.java)
            SDL_EventType.SDL_KEYMAPCHANGED -> setType(SDL_CommonEvent::class.java)
            SDL_EventType.SDL_TEXTEDITING_EXT -> setType(SDL_TextEditingExtEvent::class.java)
            SDL_EventType.SDL_MOUSEMOTION -> setType(SDL_MouseMotionEvent::class.java)
            SDL_EventType.SDL_MOUSEBUTTONDOWN, SDL_EventType.SDL_MOUSEBUTTONUP -> setType(
                SDL_MouseButtonEvent::class.java
            )

            SDL_EventType.SDL_MOUSEWHEEL -> setType(SDL_MouseWheelEvent::class.java)
            SDL_EventType.SDL_JOYAXISMOTION -> setType(SDL_JoyAxisEvent::class.java)
            SDL_EventType.SDL_JOYBALLMOTION -> setType(SDL_JoyBallEvent::class.java)
            SDL_EventType.SDL_JOYHATMOTION -> setType(SDL_JoyHatEvent::class.java)
            SDL_EventType.SDL_JOYBUTTONDOWN, SDL_EventType.SDL_JOYBUTTONUP -> setType(
                SDL_JoyButtonEvent::class.java
            )

            SDL_EventType.SDL_JOYDEVICEADDED, SDL_EventType.SDL_JOYDEVICEREMOVED -> setType(
                SDL_JoyDeviceEvent::class.java
            )

            SDL_EventType.SDL_JOYBATTERYUPDATED -> setType(SDL_JoyBatteryEvent::class.java)
            SDL_EventType.SDL_CONTROLLERAXISMOTION -> setType(SDL_ControllerAxisEvent::class.java)
            SDL_EventType.SDL_CONTROLLERBUTTONDOWN, SDL_EventType.SDL_CONTROLLERBUTTONUP -> setType(
                SDL_ControllerButtonEvent::class.java
            )

            SDL_EventType.SDL_CONTROLLERDEVICEADDED, SDL_EventType.SDL_CONTROLLERDEVICEREMOVED, SDL_EventType.SDL_CONTROLLERDEVICEREMAPPED -> setType(
                SDL_ControllerDeviceEvent::class.java
            )

            SDL_EventType.SDL_CONTROLLERTOUCHPADDOWN, SDL_EventType.SDL_CONTROLLERTOUCHPADMOTION, SDL_EventType.SDL_CONTROLLERTOUCHPADUP -> setType(
                SDL_ControllerTouchpadEvent::class.java
            )

            SDL_EventType.SDL_CONTROLLERSENSORUPDATE -> setType(SDL_ControllerSensorEvent::class.java)
            SDL_EventType.SDL_FINGERDOWN, SDL_EventType.SDL_FINGERUP, SDL_EventType.SDL_FINGERMOTION -> setType(
                SDL_TouchFingerEvent::class.java
            )

            SDL_EventType.SDL_DOLLARGESTURE, SDL_EventType.SDL_DOLLARRECORD -> setType(
                SDL_DollarGestureEvent::class.java
            )

            SDL_EventType.SDL_MULTIGESTURE -> setType(SDL_MultiGestureEvent::class.java)
            SDL_EventType.SDL_CLIPBOARDUPDATE -> setType(SDL_CommonEvent::class.java)
            SDL_EventType.SDL_DROPFILE, SDL_EventType.SDL_DROPTEXT, SDL_EventType.SDL_DROPBEGIN, SDL_EventType.SDL_DROPCOMPLETE -> setType(
                SDL_DropEvent::class.java
            )

            SDL_EventType.SDL_AUDIODEVICEADDED, SDL_EventType.SDL_AUDIODEVICEREMOVED -> setType(
                SDL_AudioDeviceEvent::class.java
            )

            SDL_EventType.SDL_SENSORUPDATE -> setType(SDL_SensorEvent::class.java)
            SDL_EventType.SDL_RENDER_TARGETS_RESET, SDL_EventType.SDL_RENDER_DEVICE_RESET -> setType(
                SDL_CommonEvent::class.java
            )

            else -> if (type >= SDL_EventType.SDL_USEREVENT && type < SDL_EventType.SDL_LASTEVENT) {
                setType(SDL_UserEvent::class.java)
            } else {
                setType(SDL_CommonEvent::class.java)
            }
        }
        super.read()
    }
}
