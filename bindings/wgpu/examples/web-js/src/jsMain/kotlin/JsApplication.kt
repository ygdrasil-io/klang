@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.ImageBitmapHolder
import io.ygdrasil.wgpu.getRenderingContext
import io.ygdrasil.wgpu.requestAdapter
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise

external fun setInterval(render: () -> Unit, updateInterval: Int)

// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toInt()

@JsExport
fun jsApplication(canvas: HTMLCanvasElement): Promise<Application> {
	return MainScope().promise {

		val assetManager = getAssetManager()

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
			adapter,
			assetManager
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

suspend fun getAssetManager(): AssetManager {
	val Di3d: ImageBitmapHolder = getImage("./assets/img/Di-3d.png")
	println(Di3d)
	return object : AssetManager {
		override val Di3d: ImageBitmapHolder = Di3d

	}
}

private suspend fun getImage(url: String): ImageBitmapHolder {
	return window.fetch(url).await()
		.blob().await()
		.let { window.createImageBitmap(it) }.await()
		.let { ImageBitmapHolder(it) }
}
