package darwin

import darwin.internal.sel

val NSScreenClass by lazy { NSClass("NSScreen") }

class NSScreen(id: Long) : NSObject(id) {

	val frame: NSRect get() = NSRect().apply { ObjectiveC.objc_msgSend_stret(this, id, sel("frame")) }

	companion object{
		fun mainScreen(): NSScreen? = NSScreen(NSScreenClass.msgSend("mainScreen"))
	}
}