@file:OptIn(ExperimentalStdlibApi::class)

import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.getRenderingContext
import io.ygdrasil.wgpu.requestAdapter
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

external fun setInterval(render: () -> Unit, updateInterval: Int)

// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toInt()

fun jsApplication() = MainScope().launch {

	val canvas = (document.getElementById("webgpu") as? HTMLCanvasElement) ?: error("fail to get canvas")
	val adapter = requestAdapter() ?: error("No appropriate Adapter found.")
	val device = adapter.requestDevice() ?: error("No appropriate Device found.")
	val renderingContext = canvas.getRenderingContext() ?: error("fail to get context")

	renderingContext.configure(
		CanvasConfiguration(device)
	)

	val application = object : Application(
		renderingContext,
		device,
		adapter
	) {
		override fun run() {
			// Schedule main loop to run repeatedly
			setInterval({
				renderFrame()
			}, UPDATE_INTERVAL);
		}
	}

	launch { application.run() }
}