package io.ygdrasil.wgpu

import com.sun.jna.NativeLong

internal fun Long.toNativeLong(): NativeLong = let(::NativeLong)
internal fun Int.toNativeLong(): NativeLong = toLong()
	.let(::NativeLong)