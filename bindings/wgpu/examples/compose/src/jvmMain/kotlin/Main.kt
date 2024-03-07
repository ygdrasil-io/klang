import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.jna.Pointer
import darwin.NSView
import darwin.NSWindow
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.examples.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.rococoa.ID
import org.rococoa.Rococoa

@Composable
@Preview
fun App() {
	var text by remember { mutableStateOf("Hello, World!") }

	MaterialTheme {
		Button(onClick = {
			text = "Hello, Desktop!"
		}) {
			Text(text)
		}
	}
}


val windowState = MutableStateFlow<ComposeWindow?>(null)


fun main() {
	val thread = Thread {
		runBlocking {
			windowState
				.filterNotNull()
				.collect { window ->
					runApp(window)
				}
		}
	}
	thread.start()


	application {
		Window(onCloseRequest = ::exitApplication) {
			windowState.update { window }

			App()
		}
	}


}

val applicationScope = CoroutineScope(Dispatchers.Default)

suspend fun runApp(window: ComposeWindow) {
	val nswindow = Rococoa.wrap(ID.fromLong(window.windowHandle), NSWindow::class.java)
	val layer = nswindow.contentView()?.layer() ?: error("fail to get layer")

	val view = NSView.create(nswindow.frame())
	view.setWantsLayer(true)
	//val test = NSString("test")
	//println("test ${test.length}")
	println("window handler ${window.windowHandle}")
	println("window hander ${nswindow.description()}")
	println("window hander ${layer.description()}")

	(WGPU.createInstance() ?: error("fail to wgpu instance")).use { instance ->


		val surface = instance.getSurfaceFromMetalLayer(Pointer(layer.id().toLong())) ?: error("fail to get surface")
		val renderingContext = RenderingContext(surface)

		val adapter = instance.requestAdapter(renderingContext)
			?: error("fail to get adapter")

		val device = adapter.requestDevice()
			?: error("fail to get device")

		renderingContext.computeSurfaceCapabilities(adapter)
		renderingContext.configure(device) {
			window.size.width to window.size.height
		}

		val application = object : Application(
			renderingContext,
			device,
			adapter
		) {
			override suspend fun run() {
				renderFrame()

				applicationScope.launch() {
					run()
				}
			}


		}
		application.run()
	}
}


