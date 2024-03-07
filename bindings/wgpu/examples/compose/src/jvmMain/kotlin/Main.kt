import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sun.jna.Pointer
import darwin.NSWindow
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.WGPU
import io.ygdrasil.wgpu.examples.Application
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import org.rococoa.ID
import org.rococoa.Rococoa

@Composable
@Preview
fun App() {

	MaterialTheme {
		Column(
			Modifier
				.width(300.dp)
				.fillMaxHeight()
				.background(MaterialTheme.colors.background)
		) {

			Card(
				Modifier
					.fillMaxWidth()
					.padding(5.dp)
			) {
				Column(
					Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colors.error)
				) {
					Text(
						"Basic Graphics",
						style = MaterialTheme.typography.h4
					)
					Button(
						onClick = {

						}
					) {
						Text(
							"Titling screen",
							style = MaterialTheme.typography.subtitle1
						)
					}
				}
			}

		}

	}
}


val windowStateFlow = MutableStateFlow<ComposeWindow?>(null)


fun main() {
	val thread = Thread {
		runBlocking {
			windowStateFlow
				.filterNotNull()
				.collect { window ->
					runApp(window)
				}
		}
	}
	thread.start()


	application {
		val windowState = rememberWindowState(
			width = 775.dp,
			height = 1500.dp,
			position = WindowPosition(0.dp, 0.dp)
		)
		Window(
			onCloseRequest = ::exitApplication,
			//alwaysOnTop = true,
			state = windowState
		) {
			windowStateFlow.update { window }

			App()
		}
	}


}

val applicationScope = CoroutineScope(Dispatchers.Default)

suspend fun runApp(window: ComposeWindow) {
	val nswindow = Rococoa.wrap(ID.fromLong(window.windowHandle), NSWindow::class.java)
	val layer = nswindow.contentView()?.layer() ?: error("fail to get layer")

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
				delay(UPDATE_INTERVAL)
				applicationScope.launch() {
					run()
				}
			}


		}
		application.run()
	}
}


// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toLong()