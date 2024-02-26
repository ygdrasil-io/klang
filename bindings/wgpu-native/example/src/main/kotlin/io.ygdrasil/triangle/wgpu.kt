package io.ygdrasil.triangle

import com.sun.jna.Pointer
import io.ygdrasil.libsdl.SDL_GetWindowWMInfo
import io.ygdrasil.libsdl.SDL_SysWMinfo
import io.ygdrasil.libsdl.SDL_Window
import libwgpu.*

const val LOG_PREFIX = "ReturnValueExtractor: "

val log_callback = object : WGPULogCallback {
	override fun invoke(param1: Int, message: String, param3: Pointer) {
		val level = WGPULogLevel.of(param1)
		val levelStr = when (level) {
			WGPULogLevel.WGPULogLevel_Error -> "error"
			WGPULogLevel.WGPULogLevel_Warn -> "warn"
			WGPULogLevel.WGPULogLevel_Info -> "info"
			WGPULogLevel.WGPULogLevel_Debug -> "debug"
			WGPULogLevel.WGPULogLevel_Trace -> "trace"
			else -> "no level"
		}
		println("[wgpu] [$levelStr] $message")
	}
}

fun frmwrk_setup_logging(level: WGPULogLevel) {
	wgpuSetLogCallback(log_callback, null)
	wgpuSetLogLevel(level.value)
}

fun handleRequestAdapter(block: (WGPUAdapter) -> Unit) = object : WGPURequestAdapterCallback {
	override fun invoke(param1: Int, adapter: WGPUAdapterImpl, message: String, param4: Pointer) {
		val status = WGPURequestAdapterStatus.of(param1)
		if (status == WGPURequestAdapterStatus.WGPURequestAdapterStatus_Success) {
			block(adapter)
		} else {
			println(LOG_PREFIX + " request_adapter status=%.8X message=%s\n".format(status, message))
		}
	}

}


fun SDL_GetWGPUSurface(instance: WGPUInstance, window: SDL_Window): WGPUSurface {
	val windowWMInfo = SDL_SysWMinfo()
	windowWMInfo.version.major = 2
	windowWMInfo.version.minor = 30
	windowWMInfo.version.patch = 0
	SDL_GetWindowWMInfo(window, windowWMInfo)
	/*NSWindow* ns_window = windowWMInfo.info.cocoa.window;
	[ns_window.contentView setWantsLayer : YES] ;
	metal_layer = [CAMetalLayer layer];
	[ns_window.contentView setLayer : metal_layer] ;*/

	TODO()
}