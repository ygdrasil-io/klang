import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.examples.blueScreen
import io.ygdrasil.wgpu.internal.js.GPUCanvasConfiguration
import io.ygdrasil.wgpu.internal.js.GPUCanvasContext
import io.ygdrasil.wgpu.internal.js.GPUDevice
import io.ygdrasil.wgpu.navigator
import io.ygdrasil.wgpu.requestAdapter
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement


external fun setInterval(render: () -> Unit, updateInterval: Int)

class CanvasConfiguration(override var device: GPUDevice, override var format: String) : GPUCanvasConfiguration

val UPDATE_INTERVAL = (1000.0 / 60.0).toInt() // 60 Frame per second
//private var blue = 0.0

fun blueCanvas() = GlobalScope.launch {
	val canvas = (document.getElementById("webgpu") as? HTMLCanvasElement) ?: error("fail to get canvas")

	val adapter = requestAdapter() ?: error("No appropriate Adapter found.")

	val device = adapter.requestDevice() ?: error("No appropriate Device found.")

	// Canvas configuration
	val context = (canvas.getContext("webgpu") as? GPUCanvasContext)
		?: error("fail to get context")
	val canvasFormat = navigator.gpu!!.getPreferredCanvasFormat()
	context.configure(
		CanvasConfiguration(
			device = device.handler,
			format = canvasFormat
		).apply {

		}
	)

	// Schedule render() to run repeatedly
	setInterval({
		blueScreen(device, RenderingContext(context))
		//render(device, context)
	}, UPDATE_INTERVAL);

}