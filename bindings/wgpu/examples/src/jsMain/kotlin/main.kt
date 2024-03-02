import io.ygdrasil.wgpu.examples.blueCanvas
import kotlinx.browser.window

fun main() {
	window.addEventListener("DOMContentLoaded", {
		blueCanvas()
	})
}