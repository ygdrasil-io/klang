package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.*

actual class Device(internal val handler: WGPUDeviceImpl) : AutoCloseable {

	val queue: Queue by lazy { Queue(wgpuDeviceGetQueue(handler) ?: error("fail to get device queue")) }

	actual fun createCommandEncoder(descriptor: CommandEncoderDescriptor?): CommandEncoder {
		return CommandEncoder(
			wgpuDeviceCreateCommandEncoder(handler, descriptor?.convert() ?: null)
				?: error("fail to create command encoder")
		)
	}

	actual fun createShaderModule(descriptor: ShaderModuleDescriptor): ShaderModule {
		return ShaderModule(
			wgpuDeviceCreateShaderModule(handler, descriptor.convert())
		)
	}


	override fun close() {
		wgpuDeviceRelease(handler)
	}

}

private fun CommandEncoderDescriptor.convert(): WGPUCommandEncoderDescriptor = WGPUCommandEncoderDescriptor().also {
	it.label = label
}
