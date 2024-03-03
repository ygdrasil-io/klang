package io.ygdrasil.wgpu

import com.sun.jna.NativeLong
import io.ygdrasil.wgpu.internal.jvm.*

actual class ShaderModule(internal val handler: WGPUShaderModule?) : AutoCloseable {
	override fun close() {
		wgpuShaderModuleRelease(handler)
	}
}


internal fun ShaderModuleDescriptor.convert(): WGPUShaderModuleDescriptor = WGPUShaderModuleDescriptor().also {
	it.label = label
	it.nextInChain = WGPUShaderModuleWGSLDescriptor.ByReference().also {
		it.code = code
		it.chain.apply {
			sType = WGPUSType.WGPUSType_ShaderModuleWGSLDescriptor.value
		}
	}
	it.hintCount = NativeLong(compilationHints.size)
	it.hints = compilationHints.map { it.convert() }

}

private fun ShaderModuleDescriptor.CompilationHint.convert() = WGPUShaderModuleCompilationHint.ByReference().also {
	TODO("no yet implemented")
}
