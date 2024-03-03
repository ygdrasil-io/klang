package io.ygdrasil.wgpu

expect class Queue {

	fun submit(commandsBuffer: Array<CommandBuffer>)

}