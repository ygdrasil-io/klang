package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
import dev.krud.shapeshift.transformer.base.MappingTransformer
import dev.krud.shapeshift.transformer.base.MappingTransformerContext
import io.ygdrasil.wgpu.internal.jvm.WGPUExtent3D

internal fun Long.toNativeLong(): NativeLong = let(::NativeLong)
internal fun Int.toNativeLong(): NativeLong = toLong()
	.let(::NativeLong)

class EnumerationTransformer<T : EnumerationWithValue> : MappingTransformer<T, Int> {
	override fun transform(context: MappingTransformerContext<out T>): Int? {
		return context.originalValue?.value
	}
}

class GPUExtent3DDictStrictTransformer : MappingTransformer<GPUExtent3DDictStrict, WGPUExtent3D> {
	override fun transform(context: MappingTransformerContext<out GPUExtent3DDictStrict>): WGPUExtent3D? =
		context.originalValue?.let {
			WGPUExtent3D().apply {
				width = it.width
				height = it.height
				depthOrArrayLayers = it.depthOrArrayLayers
			}
		}
}