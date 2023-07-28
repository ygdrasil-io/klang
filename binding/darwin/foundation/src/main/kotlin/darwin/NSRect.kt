package darwin

import com.sun.jna.Structure

@Structure.FieldOrder("x", "y", "width", "height")
open class NSRect : Structure {
	@JvmField var x: Double = 0.0
	@JvmField var y: Double = 0.0
	@JvmField var width: Double = 0.0
	@JvmField var height: Double = 0.0

	constructor() {
		allocateMemory()
		autoWrite()
	}

	constructor(x: Number, y: Number, width: Number, height: Number) : this() {
		this.x = x.toDouble()
		this.y = y.toDouble()
		this.width = width.toDouble()
		this.height = height.toDouble()
	}

	class ByReference() : NSRect(), Structure.ByReference {
		constructor(x: Number, y: Number, width: Number, height: Number) : this() {
			this.x = x.toDouble()
			this.y = y.toDouble()
			this.width = width.toDouble()
			this.height = height.toDouble()
		}
	}
	class ByValue() : NSRect(), Structure.ByValue {
		constructor(x: Number, y: Number, width: Number, height: Number) : this() {
			this.x = x.toDouble()
			this.y = y.toDouble()
			this.width = width.toDouble()
			this.height = height.toDouble()
		}
	}

	override fun toString(): String = "NSRect($x, $y, $width, $height)"
}