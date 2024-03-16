package io.ygdrasil.triangle

const val LOG_PREFIX = "ReturnValueExtractor: "
//
//val log_callback = object : WGPULogCallback {
//	override fun invoke(param1: Int, message: String, param3: Pointer) {
//		val level = WGPULogLevel.of(param1)
//		val levelStr = when (level) {
//			WGPULogLevel.WGPULogLevel_Error -> "error"
//			WGPULogLevel.WGPULogLevel_Warn -> "warn"
//			WGPULogLevel.WGPULogLevel_Info -> "info"
//			WGPULogLevel.WGPULogLevel_Debug -> "debug"
//			WGPULogLevel.WGPULogLevel_Trace -> "trace"
//			else -> "no level"
//		}
//		println("[wgpu] [$levelStr] $message")
//	}
//}
//
//fun frmwrk_setup_logging(level: WGPULogLevel) {
//	wgpuSetLogCallback(log_callback, null)
//	wgpuSetLogLevel(level.value)
//}
//

//
//
//fun SDL_GetWGPUSurface(instance: WGPUInstance, window: SDL_Window): WGPUSurface {
//	val windowWMInfo = SDL_SysWMinfo()
//	windowWMInfo.version.major = 2
//	windowWMInfo.version.minor = 30
//	windowWMInfo.version.patch = 0
//	SDL_GetWindowWMInfo(window, windowWMInfo)
//	/*NSWindow* ns_window = windowWMInfo.info.cocoa.window;
//	[ns_window.contentView setWantsLayer : YES] ;
//	metal_layer = [CAMetalLayer layer];
//	[ns_window.contentView setLayer : metal_layer] ;*/
//
//	TODO()
//}