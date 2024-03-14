import com.sun.jna.Pointer
import darwin.NSWindow
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.WGPU.Companion.createInstance
import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.examples.Os
import io.ygdrasil.wgpu.examples.Platform
import io.ygdrasil.wgpu.internal.jvm.WGPUSurface
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.system.MemoryUtil.NULL
import org.rococoa.ID
import org.rococoa.Rococoa
import kotlin.system.exitProcess

suspend fun main() {
	var width = 640
	var height = 480

	glfwInit()
	glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
	glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
	val windowHandle: Long = glfwCreateWindow(width, height, "LWJGL Demo", NULL, NULL)
	glfwMakeContextCurrent(windowHandle)
	glfwSwapInterval(1)

	val glfwDispatcher = GlfwCoroutineDispatcher() // a custom coroutine dispatcher, in which Compose will run

	glfwSetWindowCloseCallback(windowHandle) {
		glfwDispatcher.stop()
	}

	val wgpu = createInstance() ?: error("fail to wgpu instance")
	val surface = wgpu.getSurface(windowHandle)

	val renderingContext = RenderingContext(surface) {
		val width = intArrayOf(1)
		val height = intArrayOf(1)
		glfwGetWindowSize(windowHandle, width, height)
		width[0] to height[0]
	}

	val adapter = wgpu.requestAdapter(renderingContext)
		?: error("fail to get adapter")

	val device = adapter.requestDevice()
		?: error("fail to get device")

	renderingContext.computeSurfaceCapabilities(adapter)
	renderingContext.configure(device)

	val application = object : Application() {

		override fun run() {
			TODO("Not yet implemented")
		}

	}

	fun render() {

		application.configureRenderingContext()
		application.renderFrame()
		glfwSwapBuffers(windowHandle)
	}


	glfwSetWindowSizeCallback(windowHandle) { _, windowWidth, windowHeight ->
		width = windowWidth
		height = windowHeight


		glfwSwapInterval(0)
		render()
		glfwSwapInterval(1)
	}


	glfwShowWindow(windowHandle)

	application.run()
	glfwDispatcher.runLoop()

	application.close()
	wgpu.close()
	glfwDestroyWindow(windowHandle)
	exitProcess(0)
}

fun WGPU.getSurface(window: Long): WGPUSurface = when (Platform.os) {
	Os.Linux -> TODO()
	Os.Window -> TODO()
	Os.MacOs -> {
		val nsWindowPtr = glfwGetCocoaWindow(window)
		val nswindow = Rococoa.wrap(ID.fromLong(nsWindowPtr), NSWindow::class.java)
		val layer = nswindow.contentView()?.layer() ?: error("fail to get layer")
		getSurfaceFromMetalLayer(Pointer(layer.id().toLong())) ?: error("fail to get surface on MacOs")
	}
}

private fun glfwGetWindowContentScale(window: Long): Float {
	val array = FloatArray(1)
	glfwGetWindowContentScale(window, array, FloatArray(1))
	return array[0]
}

