import darwin.*

val NSWindowStyleMaskTitled = 1 shl 0
val NSWindowStyleMaskClosable = 1 shl 1
val NSWindowStyleMaskMiniaturizable = 1 shl 2
val NSWindowStyleMaskResizable = 1 shl 3
val NSBackingStoreBuffered = 2
val windowStyle = NSWindowStyleMaskTitled or NSWindowStyleMaskMiniaturizable or
	NSWindowStyleMaskClosable or NSWindowStyleMaskResizable

fun main() {

	nsAutoreleasePool {
		val application = NSApplication.sharedApplication()

		val frame = NSScreen.mainScreen()!!.frame
		val windowRect = NSRect().apply {
			x = 0.0
			y = 0.0
			width = frame.width * 0.5
			height = frame.height * 0.5
		}

		val window = NSWindow(windowRect, windowStyle, NSBackingStoreBuffered, false)
		val device = MTLCreateSystemDefaultDevice() ?: error("fail to create device")
		println(window.id)
		/*val mtkView = MTKView(windowRect, device).apply {
			colorPixelFormat = MTLPixelFormatBGRA8Unorm_sRGB
			clearColor = MTLClearColorMake(0.0, 0.0, 0.0, 1.0)
		}
		val renderer = rendererProvider(mtkView)

		application.delegate = object : NSObject(), NSApplicationDelegateProtocol {

			override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean {
				println("applicationShouldTerminateAfterLastWindowClosed")
				return true
			}

			override fun applicationWillFinishLaunching(notification: NSNotification) {
				println("applicationWillFinishLaunching")
			}

			override fun applicationDidFinishLaunching(notification: NSNotification) {
				println("applicationDidFinishLaunching")

				mtkView.delegate = object : NSObject(), MTKViewDelegateProtocol {
					override fun drawInMTKView(view: MTKView) {
						renderer.drawOnView(view)
					}

					override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) {

					}

				}

				window.setContentView(mtkView)
				window.setTitle(windowTitle)


				window.orderFrontRegardless()
				window.center()
				window.level = NSFloatingWindowLevel
			}

			override fun applicationWillTerminate(notification: NSNotification) {
				println("applicationWillTerminate")
				// Insert code here to tear down your application
			}
		}
		*/
		application.run()
	}
}
