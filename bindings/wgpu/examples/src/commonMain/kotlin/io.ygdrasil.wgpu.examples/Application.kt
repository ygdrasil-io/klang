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

	private lateinit var currentScene: Scene

	init {
		changeScene(scenes.first())
	}

	abstract class Scene {

		abstract fun Application.initialiaze()

		abstract fun Application.render()

	}

	fun changeScene(nextScene: Scene) {
		with(nextScene) {
			initialiaze()
		}
		currentScene = nextScene
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
	SimpleTriangleScene(),
	BlueTitlingScene()
)