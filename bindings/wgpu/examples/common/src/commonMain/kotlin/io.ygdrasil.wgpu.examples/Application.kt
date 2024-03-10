@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.examples.scenes.basic.RotatingCubeScene

abstract class Application(
	val renderingContext: RenderingContext,
	val device: Device,
	val adapter: Adapter
) : AutoCloseable {

	private lateinit var currentScene: Scene
	private var onError = false

	var frame = 0
		private set

	init {
		changeScene(availableScenes.first())
	}

	abstract class Scene {

		abstract fun Application.initialiaze()

		abstract fun Application.render()

	}

	fun changeScene(nextScene: Scene) {
		with(nextScene) {
			try {
				initialiaze()
			} catch (e: Throwable) {
				e.printStackTrace()
				onError = true
				throw e
			}
		}
		currentScene = nextScene
	}

	fun renderFrame() {
		if (onError) return
		frame += 1
		with(currentScene) {
			try {
				render()
			} catch (e: Throwable) {
				e.printStackTrace()
				onError = true
				throw e
			}
		}
	}

	override fun close() {
		renderingContext.close()
		device.close()
		adapter.close()
	}

	abstract suspend fun run()
}

val availableScenes = listOf(
	RotatingCubeScene(),
	SimpleTriangleScene(),
	BlueTitlingScene(),
)