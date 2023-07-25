package darwin.internal

import com.sun.jna.FunctionMapper
import com.sun.jna.Library
import com.sun.jna.Native
import darwin.ObjectiveC

internal annotation class NativeName(val name: String) {
	companion object {
		val OPTIONS = mapOf(
			Library.OPTION_FUNCTION_MAPPER to FunctionMapper { _, method ->
				method.getAnnotation(NativeName::class.java)?.name ?: method.name
			}
		)
	}
}

internal inline fun <reified T : Library> NativeLoad(name: String): T =
	Native.load(name, T::class.java, NativeName.OPTIONS) as T

internal typealias ID = Long

internal fun Long.msgSend(sel: String, vararg args: Any?): Long = ObjectiveC.objc_msgSend(this, sel(sel), *args)

internal fun sel(name: String): Long = ObjectiveC
	.sel_registerName(name)
	.takeIf { it != 0L }
	?: error("Invalid selector '$name'")