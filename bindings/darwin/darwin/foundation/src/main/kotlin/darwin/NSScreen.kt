package darwin

import darwin.internal.sel

val NSScreen2Class by lazy { NSClass("NSScreen") }

class NSScreen2(id: Long) : NSObject(id) {

	val frame: NSRect get() = NSRect().apply { ObjectiveC.objc_msgSend_stret(this, id, sel("frame")) }

	companion object{
		fun mainScreen(): NSScreen2? = NSScreen2(NSScreen2Class.msgSend("mainScreen"))
	}
}