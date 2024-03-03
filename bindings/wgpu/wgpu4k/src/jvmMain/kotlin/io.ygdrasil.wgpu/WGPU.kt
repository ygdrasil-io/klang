package io.ygdrasil.wgpu.examples.io.ygdrasil.wgpu

import com.sun.jna.Pointer
import io.ygdrasil.libsdl.SDL_Window
import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.internal.jvm.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class WGPU(private val handler: WGPUInstance) : AutoCloseable {
	override fun close() {
		wgpuInstanceRelease(handler)
	}

	suspend fun requestAdapter(options: WGPURequestAdapterOptions): Adapter? {
		val adapterState = MutableStateFlow<WGPUAdapterImpl?>(null)

		val handleRequestAdapter = object : WGPURequestAdapterCallback {
			override fun invoke(statusAsInt: Int, adapter: WGPUAdapterImpl, message: String?, param4: Pointer?) {
				val status = WGPURequestAdapterStatus.of(statusAsInt)
				if (status == WGPURequestAdapterStatus.WGPURequestAdapterStatus_Success) {
					adapterState.update { adapter }
				} else {
					println("request_adapter status=%.8X message=%s\n".format(status, message))
				}
			}
		}
		wgpuInstanceRequestAdapter(handler, options, handleRequestAdapter, null)

		return adapterState.value?.let { Adapter(it) }
	}

	fun getSurface(window: SDL_Window): WGPUSurface? {
		return SDL_GetWGPUSurface(handler, window)
	}


	companion object {
		fun createInstance() = wgpuCreateInstance(null)
			?.let { WGPU(it) }
	}
}