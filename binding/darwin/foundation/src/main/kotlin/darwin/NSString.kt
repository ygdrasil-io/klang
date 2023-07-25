package darwin

import darwin.internal.msgSend

open class NSString(id: Long) : NSObject(id) {
	constructor() : this("")
	constructor(id: Long?) : this(id ?: 0L)
	constructor(str: String) : this(OBJ_CLASS.msgSend("alloc").msgSend("initWithCharacters:length:", str.toCharArray(), str.length))

	//val length: Int get() = ObjectiveC.object_getIvar(this.id, LENGTH_ivar).toInt()
	val length: Int get() = this.msgSend("length").toInt()

	val cString: String
		get() {
			val length = this.length
			val ba = ByteArray(length + 1)
			msgSend("getCString:maxLength:encoding:", ba, length + 1, 4)
			val str = ba.toString(Charsets.UTF_8)
			return str.substring(0, str.length - 1)
		}

	override fun toString(): String = cString

	companion object : NSClass("NSString") {
		val LENGTH_ivar = ObjectiveC.class_getProperty(OBJ_CLASS, "length")
	}
}