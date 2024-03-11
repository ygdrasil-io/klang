@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples.scenes.basic

import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.examples.AutoClosableContext

class TexturedCubeScene : Application.Scene(), AutoCloseable {

	val autoClosableContext = AutoClosableContext()

	override fun Application.initialiaze() {
		TODO("Not yet implemented")
	}

	override fun Application.render() {
		TODO("Not yet implemented")
	}

	override fun close() {
		autoClosableContext.close()
	}

}