package io.ygdrasil.wgpu

import com.sun.jna.NativeLong

internal fun Long.toNativeLong(): NativeLong = let(::NativeLong)