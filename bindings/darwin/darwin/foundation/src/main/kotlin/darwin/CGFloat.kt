package darwin

import com.sun.jna.FromNativeContext
import com.sun.jna.Native
import com.sun.jna.NativeMapped
import com.sun.jna.Structure

@Structure.FieldOrder("value")
class CGFloat(val value: Double) : Number(), NativeMapped {
	constructor() : this(0.0)
	constructor(value: Float) : this(value.toDouble())
	constructor(value: Number) : this(value.toDouble())

	companion object {
		@JvmStatic
		val SIZE = Native.LONG_SIZE
	}

	override fun toByte(): Byte = value.toInt().toByte()
	override fun toChar(): Char = value.toInt().toChar()
	override fun toDouble(): Double = value
	override fun toFloat(): Float = value.toFloat()
	override fun toInt(): Int = value.toInt()
	override fun toLong(): Long = value.toLong()
	override fun toShort(): Short = value.toInt().toShort()
	override fun nativeType(): Class<*> = when (SIZE) {
		4 -> Float::class.java
		8 -> Double::class.java
		else -> TODO()
	}

	override fun toNative(): Any = when (SIZE) {
		4 -> this.toFloat()
		8 -> this.toDouble()
		else -> TODO()
	}

	override fun fromNative(nativeValue: Any, context: FromNativeContext?): Any = CGFloat((nativeValue as Number).toDouble())

	override fun toString(): String = "$value"
}