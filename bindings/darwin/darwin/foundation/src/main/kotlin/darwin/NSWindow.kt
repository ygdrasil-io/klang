package darwin

import darwin.internal.msgSend

val NSWindowClass2 by lazy { NSClass("NSWindow") }

class NSWindow2(id: Long) : NSObject(id) {

	constructor(id: NSRect, windowStyle: Int, NSBackingStoreBuffered: Int, b: Boolean)
		: this(NSWindowClass2.alloc().msgSend("initWithContentRect:styleMask:backing:defer:", id, windowStyle, NSBackingStoreBuffered, b))

}