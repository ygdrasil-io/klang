package example.toolkit

import javax.swing.JFrame

abstract class SampleApplication(title: String) {

	val window = OSWindow(title)
	val glWindow = EGLWindow(window)

	abstract fun initialize(): Boolean
	abstract fun destroy()
	abstract fun draw()

	fun run(): Int {

		val configParams = ConfigParameters(
			redBits = 8,
			greenBits = 8,
			blueBits = 8,
			alphaBits = 8,
			depthBits = 24,
			stencilBits = 8
		)

		if (!glWindow.initializeGL(
				mOSWindow,
				mEntryPointsLib.get(),
				mDriverType,
				mPlatformParams,
				configParams
			)
		) {
			return -1
		}

		// Disable vsync
		if (!mGLWindow.setSwapInterval(0)) {
			return -1
		}

		mRunning = true
		var result = 0

		if (!initialize()) {
			mRunning = false
			result = -1
		}

		mTimer.start()
		var prevTime = 0.0

		while (mRunning) {
			val elapsedTime = mTimer.getElapsedWallClockTime()
			val deltaTime = elapsedTime - prevTime

			step(deltaTime.toFloat(), elapsedTime)

			// Clear events that the application did not process from this frame
			var event: Event
			while (popEvent(event)) {
				// If the application did not catch a close event, close now
				when (event.type) {
					Event.Type.EVENT_CLOSED -> exit()
					Event.Type.EVENT_KEY_RELEASED -> onKeyUp(event.key)
					Event.Type.EVENT_KEY_PRESSED -> onKeyDown(event.key)
					else -> {
					}
				}
			}

			if (!mRunning) {
				break
			}

			draw()
			swap()

			mOSWindow.messageLoop()

			prevTime = elapsedTime

			mFrameCount++

			if (mFrameCount % 100 == 0) {
				printf(
					"Rate: %0.2lf frames / second\n",
					mFrameCount.toDouble() / mTimer.getElapsedWallClockTime()
				)
			}
		}

		destroy()
		mGLWindow.destroyGL()
		mOSWindow.destroy()

		return result
	}

}

class OSWindow(title: String): JFrame(title) {

	init {
	    preferredSize = java.awt.Dimension(800, 600)
		pack()
	}

}