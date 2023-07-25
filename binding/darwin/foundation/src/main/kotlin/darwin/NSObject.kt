package darwin

import com.sun.jna.FromNativeContext
import com.sun.jna.NativeMapped
import darwin.internal.sel

open class NSObject(val id: Long) : NativeMapped {
	fun msgSend(sel: String, vararg args: Any?): Long = ObjectiveC.objc_msgSend(id, sel(sel), *args)
	fun msgSendInt(sel: String, vararg args: Any?): Int = ObjectiveC.objc_msgSendInt(id, sel(sel), *args)
	fun msgSendCGFloat(sel: String, vararg args: Any?): CGFloat = ObjectiveC.objc_msgSendCGFloat(id, sel(sel), *args)
	fun msgSend_stret(sel: String, vararg args: Any?): Unit = ObjectiveC.objc_msgSend_stret(id, sel(sel), *args)

	fun alloc(): Long = msgSend("alloc")

	companion object : NSClass("NSObject")

	fun toByte(): Byte = id.toByte()
	fun toChar(): Char = id.toInt().toChar()
	fun toShort(): Short = id.toShort()
	fun toInt(): Int = id.toInt()
	fun toLong(): Long = id

	override fun toNative(): Any = this.id

	override fun fromNative(nativeValue: Any, context: FromNativeContext?): Any = NSObject((nativeValue as Number).toLong())
	override fun nativeType(): Class<*> = Long::class.javaPrimitiveType!!
}


