import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
import libangle.EGLDisplay
import libangle.EGLSurface
import libangle.libEGLLibrary
import java.awt.Component
import java.lang.reflect.Field
import javax.swing.JFrame


fun main() {
	val frame = JFrame("test").apply {
		setSize(800, 600)
		pack()
		isVisible = true
	}
	val windowId = Native.getComponentID(frame)
	println("windowId: $windowId")
	val field: Field = Component::class.java.getDeclaredField("peer")
	field.setAccessible(true)
	val peer = field.get(frame)
	println("peer: $peer")
	//frame.peer
	//val context = Context(frame)

}


class Window : JFrame("test") {

}


class Context(val mDisplay: EGLDisplay) {

	lateinit var mSurface: EGLSurface

	init {
		val majorVersion = IntByReference()
		majorVersion.value = 3
		val minorVersion = IntByReference()
		majorVersion.value = 2
		libEGLLibrary.eglInitialize(mDisplay, majorVersion.pointer, minorVersion.pointer)
	}

	fun swap() {
		libEGLLibrary.eglSwapBuffers(mDisplay, mSurface)
	}
}