package io.ygdrasil.wgpu.examples

import com.sun.jna.Pointer
import darwin.CAMetalLayer
import darwin.NSWindow
import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.ImageBitmapHolder
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.WGPU.Companion.createInstance
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
	renderingContext.configure(CanvasConfiguration(device))

	val assetManager = object : AssetManager {
		override val Di3d: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapPosx: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapNegx: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapPosy: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapNegy: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapPosz: ImageBitmapHolder
			get() = TODO("Not yet implemented")
		override val cubemapNegz: ImageBitmapHolder
			get() = TODO("Not yet implemented")

	}

	val application = object : Application(
		renderingContext,
		device,
		adapter,
		assetManager
	) {

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
		nswindow.contentView()?.setWantsLayer(true)
		val layer = CAMetalLayer.layer()
		nswindow.contentView()?.setLayer(layer.id().toLong().toPointer())
		getSurfaceFromMetalLayer(Pointer(layer.id().toLong())) ?: error("fail to get surface on MacOs")
	}
}

private fun Long.toPointer(): Pointer = Pointer(this)

