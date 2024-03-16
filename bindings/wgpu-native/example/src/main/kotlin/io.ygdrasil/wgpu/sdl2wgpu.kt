package io.ygdrasil.wgpu

import com.sun.jna.Library
import io.ygdrasil.libsdl.SDL_Window
import libwgpu.WGPUInstance
import libwgpu.WGPUSurface

public val libsdl2wgpu: Libsdl2wgpu by lazy {
	klang.internal.NativeLoad<Libsdl2wgpu>("sdl2wgpu")
}

interface Libsdl2wgpu : Library {
	 fun SDL_GetWGPUSurface(instance: WGPUInstance, window: SDL_Window): WGPUSurface?
}

fun SDL_GetWGPUSurface(instance: WGPUInstance, window: SDL_Window): WGPUSurface? = libsdl2wgpu.SDL_GetWGPUSurface(instance, window)