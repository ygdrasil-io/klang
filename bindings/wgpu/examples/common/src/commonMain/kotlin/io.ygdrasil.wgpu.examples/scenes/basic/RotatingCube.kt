package io.ygdrasil.wgpu.examples.scenes.basic

import io.ygdrasil.wgpu.BufferDescriptor
import io.ygdrasil.wgpu.BufferUsage
import io.ygdrasil.wgpu.RenderPassDescriptor
import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.examples.autoClosableContext


class RotatingCubeScene : Application.Scene() {

	override fun Application.initialiaze() {
		val verticesBuffer = device.createBuffer(
			BufferDescriptor(
				size = cubeVertexArray.size,
				usage = BufferUsage.vertex.value,
				mappedAtCreation = true
			)
		)
	}

	override fun Application.render() = autoClosableContext {


		// Clear the canvas with a render pass
		val encoder = device.createCommandEncoder()
			.bind()

		val texture = renderingContext.getCurrentTexture()
			.bind()
		val view = texture.createView()
			.bind()

		val renderPassEncoder = encoder.beginRenderPass(
			RenderPassDescriptor(
				colorAttachments = arrayOf(
					RenderPassDescriptor.ColorAttachment(
						view = view,
						loadOp = "clear",
						clearValue = arrayOf(0, 0, 0, 1.0),
						storeOp = "store"
					)
				)
			)
		).bind()
		renderPassEncoder.end()

		val commandBuffer = encoder.finish()
			.bind()

		device.queue.submit(arrayOf(commandBuffer))

		renderingContext.present()

	}

}

val cubeVertexSize = 4 * 10 // Byte size of one cube vertex.
val cubePositionOffset = 0
val cubeColorOffset = 4 * 4 // Byte offset of cube vertex color attribute.
val cubeUVOffset = 4 * 8
val cubeVertexCount = 36

val cubeVertexArray = arrayOf(
	// float4 position, float4 color, float2 uv,
	1, -1, 1, 1, 1, 0, 1, 1, 0, 1,
	-1, -1, 1, 1, 0, 0, 1, 1, 1, 1,
	-1, -1, -1, 1, 0, 0, 0, 1, 1, 0,
	1, -1, -1, 1, 1, 0, 0, 1, 0, 0,
	1, -1, 1, 1, 1, 0, 1, 1, 0, 1,
	-1, -1, -1, 1, 0, 0, 0, 1, 1, 0,

	1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
	1, -1, 1, 1, 1, 0, 1, 1, 1, 1,
	1, -1, -1, 1, 1, 0, 0, 1, 1, 0,
	1, 1, -1, 1, 1, 1, 0, 1, 0, 0,
	1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
	1, -1, -1, 1, 1, 0, 0, 1, 1, 0,

	-1, 1, 1, 1, 0, 1, 1, 1, 0, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, -1, 1, 1, 1, 0, 1, 1, 0,
	-1, 1, -1, 1, 0, 1, 0, 1, 0, 0,
	-1, 1, 1, 1, 0, 1, 1, 1, 0, 1,
	1, 1, -1, 1, 1, 1, 0, 1, 1, 0,

	-1, -1, 1, 1, 0, 0, 1, 1, 0, 1,
	-1, 1, 1, 1, 0, 1, 1, 1, 1, 1,
	-1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
	-1, -1, -1, 1, 0, 0, 0, 1, 0, 0,
	-1, -1, 1, 1, 0, 0, 1, 1, 0, 1,
	-1, 1, -1, 1, 0, 1, 0, 1, 1, 0,

	1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
	-1, 1, 1, 1, 0, 1, 1, 1, 1, 1,
	-1, -1, 1, 1, 0, 0, 1, 1, 1, 0,
	-1, -1, 1, 1, 0, 0, 1, 1, 1, 0,
	1, -1, 1, 1, 1, 0, 1, 1, 0, 0,
	1, 1, 1, 1, 1, 1, 1, 1, 0, 1,

	1, -1, -1, 1, 1, 0, 0, 1, 0, 1,
	-1, -1, -1, 1, 0, 0, 0, 1, 1, 1,
	-1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
	1, 1, -1, 1, 1, 1, 0, 1, 0, 0,
	1, -1, -1, 1, 1, 0, 0, 1, 0, 1,
	-1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
).map { it.toFloat() }.toTypedArray()


private const val vertex = """
struct Uniforms {
  modelViewProjectionMatrix : mat4x4<f32>,
}
@binding(0) @group(0) var<uniform> uniforms : Uniforms;

struct VertexOutput {
  @builtin(position) Position : vec4<f32>,
  @location(0) fragUV : vec2<f32>,
  @location(1) fragPosition: vec4<f32>,
}

@vertex
fn main(
  @location(0) position : vec4<f32>,
  @location(1) uv : vec2<f32>
) -> VertexOutput {
  var output : VertexOutput;
  output.Position = uniforms.modelViewProjectionMatrix * position;
  output.fragUV = uv;
  output.fragPosition = 0.5 * (position + vec4(1.0, 1.0, 1.0, 1.0));
  return output;
}

"""

private const val fragment = """
	@fragment
fn main(
  @location(0) fragUV: vec2<f32>,
  @location(1) fragPosition: vec4<f32>
) -> @location(0) vec4<f32> {
  return fragPosition;
}
"""