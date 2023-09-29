import com.sun.jna.ptr.IntByReference
import libangle.EGLDisplay
import libangle.EGLSurface
import libangle.libEGLLibrary
import javax.swing.JFrame

fun main() {
	val frame = JFrame("test")
	//val context = Context(frame)

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