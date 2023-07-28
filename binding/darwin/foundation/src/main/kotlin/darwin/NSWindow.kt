package darwin

import darwin.internal.msgSend

val NSWindowClass by lazy { NSClass("NSWindow") }

class NSWindow(id: Long) : NSObject(id) {

	constructor(id: NSRect, windowStyle: Int, NSBackingStoreBuffered: Int, b: Boolean)
		: this(NSWindowClass.alloc().msgSend("initWithContentRect:styleMask:backing:defer:", id, windowStyle, NSBackingStoreBuffered, b))

}