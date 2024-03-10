@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples.scenes.basic

import io.ygdrasil.wgpu.*
import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.examples.AutoClosableContext
import io.ygdrasil.wgpu.examples.autoClosableContext
import korlibs.math.geom.Angle
import korlibs.math.geom.Matrix4
import korlibs.memory.setArrayBE
import kotlin.js.JsExport
import kotlin.math.PI

@JsExport
class RotatingCubeScene : Application.Scene(), AutoCloseable {

	lateinit var renderPipeline: RenderPipeline
	lateinit var projectionMatrix: Matrix4
	lateinit var renderPassDescriptor: RenderPassDescriptor
	lateinit var uniformBuffer: Buffer
	lateinit var uniformBindGroup: BindGroup
	lateinit var verticesBuffer: Buffer

	val autoClosableContext = AutoClosableContext()

	override fun Application.initialiaze() = with(autoClosableContext) {

		val dummyTexture = device.createTexture(
			TextureDescriptor(
				size = 1 to 1,
				format = TextureFormat.depth24plus,
				usage = TextureUsage.renderattachment.value,
			)
		).bind()

		// Create a vertex buffer from the cube data.
		verticesBuffer = device.createBuffer(
			BufferDescriptor(
				size = (cubeVertexArray.size * Float.SIZE_BYTES).toLong(),
				usage = BufferUsage.vertex.value,
				mappedAtCreation = true
			)
		)
		val test = verticesBuffer.getMappedRange()
		test.setArrayBE(0, cubeVertexArray)

		verticesBuffer.unmap()

		renderPipeline = device.createRenderPipeline(
			RenderPipelineDescriptor(
				vertex = RenderPipelineDescriptor.VertexState(
					module = device.createShaderModule(
						ShaderModuleDescriptor(
							code = vertex
						)
					).bind(), // bind to autoClosableContext to release it later
					buffers = arrayOf(
						RenderPipelineDescriptor.VertexState.VertexBufferLayout(
							arrayStride = cubeVertexSize,
							attributes = arrayOf(
								RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
									shaderLocation = 0,
									offset = cubePositionOffset,
									format = "float32x4"
								),
								RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
									shaderLocation = 1,
									offset = cubeUVOffset,
									format = "float32x2"
								)
							)
						)
					)
				),
				fragment = RenderPipelineDescriptor.FragmentState(
					module = device.createShaderModule(
						ShaderModuleDescriptor(
							code = fragment
						)
					).bind(), // bind to autoClosableContext to release it later
					targets = arrayOf(
						RenderPipelineDescriptor.FragmentState.ColorTargetState(
							format = renderingContext.textureFormat
						)
					)
				),
				primitive = RenderPipelineDescriptor.PrimitiveState(
					topology = PrimitiveTopology.trianglelist,
					cullMode = CullMode.back
				),
				depthStencil = RenderPipelineDescriptor.DepthStencilState(
					depthWriteEnabled = true,
					depthCompare = "less",
					format = TextureFormat.depth24plus
				)
			)
		).bind()

		val depthTexture = device.createTexture(
			TextureDescriptor(
				size = renderingContext.width to renderingContext.height,
				format = TextureFormat.depth24plus,
				usage = TextureUsage.renderattachment.value,
			)
		).bind()

		val uniformBufferSize = 4L * 16L; // 4x4 matrix
		uniformBuffer = device.createBuffer(
			BufferDescriptor(
				size = uniformBufferSize,
				usage = BufferUsage.uniform or BufferUsage.copydst
			)
		).bind()

		uniformBindGroup = device.createBindGroup(
			BindGroupDescriptor(
				layout = renderPipeline.getBindGroupLayout(0),
				entries = arrayOf(
					BindGroupDescriptor.BindGroupEntry(
						binding = 0,
						buffer = uniformBuffer
					)
				)
			)
		)

		renderPassDescriptor = RenderPassDescriptor(
			colorAttachments = arrayOf(
				RenderPassDescriptor.ColorAttachment(
					view = dummyTexture.createView().bind(), // Assigned later
					loadOp = "clear",
					clearValue = arrayOf(0.5, 0.5, 0.5, 1.0),
					storeOp = "store",
				)
			),
			depthStencilAttachment = RenderPassDescriptor.RenderPassDepthStencilAttachment(
				view = depthTexture.createView(),
				depthClearValue = 1.0f,
				depthLoadOp = LoadOp.clear,
				depthStoreOp = StoreOp.store
			)
		)


		val aspect = renderingContext.width / renderingContext.height.toDouble()
		val fox = Angle.fromRadians((2 * PI) / 5)
		projectionMatrix = Matrix4.perspective(fox, aspect, 1.0, 100.0)
	}

	override fun Application.render() = autoClosableContext {

		val transformationMatrix = getTransformationMatrix(
			frame / 1000.0,
			projectionMatrix
		)
		device.queue.writeBuffer(
			uniformBuffer,
			0,
			transformationMatrix,
			0,
			transformationMatrix.size.toLong()
		)

		renderPassDescriptor.colorAttachments[0].view = renderingContext
			.getCurrentTexture()
			.bind()
			.createView()

		val encoder = device.createCommandEncoder()
			.bind()

		val renderPassEncoder = encoder.beginRenderPass(renderPassDescriptor)
			.bind()
		renderPassEncoder.setPipeline(renderPipeline)
		renderPassEncoder.setBindGroup(0, uniformBindGroup)
		renderPassEncoder.setVertexBuffer(0, verticesBuffer)
		renderPassEncoder.draw(cubeVertexCount)
		renderPassEncoder.end()

		val commandBuffer = encoder.finish()
			.bind()

		device.queue.submit(arrayOf(commandBuffer))

		renderingContext.present()

	}

	override fun close() {
		autoClosableContext.close()
	}


	companion object {
		fun getTransformationMatrix(angle: Double, projectionMatrix: Matrix4): FloatArray {
			var viewMatrix = Matrix4.IDENTITY
			viewMatrix = viewMatrix.translated(0, 0, -4)

			viewMatrix = viewMatrix.rotated(
				Angle.fromRadians(Angle.fromRadians(angle).sine),
				Angle.fromRadians(Angle.fromRadians(angle).cosine),
				Angle.fromRadians(0)
			)

			return (projectionMatrix * viewMatrix).copyToColumns()
		}

		val cubeVertexSize = 4L * 10L // Byte size of one cube vertex.
		val cubePositionOffset = 0L
		val cubeColorOffset = 4 * 4 // Byte offset of cube vertex color attribute.
		val cubeUVOffset = 4L * 8L
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
		).let { FloatArray(it.size) { index -> it[index].toFloat() } }


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
	}
}



