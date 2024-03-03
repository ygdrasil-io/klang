@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.RenderingContext

abstract class Application(
	val renderingContext: RenderingContext,
	val device: Device,
	val adapter: Adapter
) : AutoCloseable {

	var currentScene: Scene = scenes.first()

	abstract class Scene {

		abstract fun Application.initialiaze()

		abstract fun Application.render()

	}

	fun renderFrame() {
		with(currentScene) {
			render()
		}
	}

	override fun close() {
		renderingContext.close()
		device.close()
		adapter.close()
	}


	abstract fun run()
}

val scenes = listOf(
	BlueTitlingScene()
)