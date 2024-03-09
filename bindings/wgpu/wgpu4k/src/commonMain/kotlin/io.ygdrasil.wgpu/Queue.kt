package io.ygdrasil.wgpu

expect class Queue {

	fun submit(commandsBuffer: Array<CommandBuffer>)

	fun writeBuffer(buffer: Buffer, bufferOffset: GPUSize64, data: FloatArray, dataOffset: GPUSize64, size: GPUSize64)

}