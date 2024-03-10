@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.getRenderingContext
import io.ygdrasil.wgpu.requestAdapter
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise

external fun setInterval(render: () -> Unit, updateInterval: Int)

// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toInt()

@JsExport
fun jsApplication(canvas: HTMLCanvasElement): Promise<Application> {
	return MainScope().promise {

		val devicePixelRatio = window.devicePixelRatio
		canvas.width = (canvas.clientWidth * devicePixelRatio).toInt()
		canvas.height = (canvas.clientHeight * devicePixelRatio).toInt()
		println("${canvas.width} ${canvas.height}")
		val adapter = requestAdapter() ?: error("No appropriate Adapter found.")
		val device = adapter.requestDevice() ?: error("No appropriate Device found.")
		val renderingContext = canvas.getRenderingContext() ?: error("fail to get context")

		renderingContext.configure(
			CanvasConfiguration(
				device = device,
				alphaMode = "premultiplied"
			)
		)

		object : Application(
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
	}

}
