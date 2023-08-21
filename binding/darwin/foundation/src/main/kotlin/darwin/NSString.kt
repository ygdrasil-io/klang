package darwin

import darwin.internal.msgSend

val NSStringClass by lazy { NSClass("NSString") }

open class NSString(id: Long) : NSObject(id) {
	constructor() : this("")
	constructor(str: String) : this(NSStringClass.alloc().msgSend("initWithCharacters:length:", str.toCharArray(), str.length))

	val length: Int get() = this.msgSend("length").toInt()
}


