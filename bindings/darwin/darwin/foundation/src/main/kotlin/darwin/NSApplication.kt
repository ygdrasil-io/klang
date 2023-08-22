package darwin

val NSApplicationClass by lazy { NSClass("NSApplication") }

class NSApplication(id: Long) : NSObject(id) {


	fun run() = msgSend("run")

	companion object{
		fun sharedApplication(): NSApplication = NSApplication(NSApplicationClass.msgSend("sharedApplication"))
	}
}