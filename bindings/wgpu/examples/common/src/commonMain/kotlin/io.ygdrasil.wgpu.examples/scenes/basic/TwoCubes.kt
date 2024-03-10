@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu.examples.scenes.basic

import io.ygdrasil.wgpu.*
import io.ygdrasil.wgpu.examples.Application
import io.ygdrasil.wgpu.examples.AutoClosableContext
import io.ygdrasil.wgpu.examples.autoClosableContext
import io.ygdrasil.wgpu.examples.scenes.basic.RotatingCubeScene.Companion.cubeVertexCount
import korlibs.math.geom.Angle
import korlibs.math.geom.Matrix4
import kotlin.math.PI

class TwoCubesScene : Application.Scene(), AutoCloseable {

	val offset = 256L; // uniformBindGroup offset must be 256-byte aligned

	lateinit var renderPipeline: RenderPipeline
	lateinit var projectionMatrix1: Matrix4
	lateinit var projectionMatrix2: Matrix4
	lateinit var renderPassDescriptor: RenderPassDescriptor
	lateinit var uniformBuffer: Buffer
	lateinit var uniformBindGroup1: BindGroup
	lateinit var uniformBindGroup2: BindGroup
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
				size = (RotatingCubeScene.cubeVertexArray.size * Float.SIZE_BYTES).toLong(),
				usage = BufferUsage.vertex.value,
				mappedAtCreation = true
			)
		)

		// Util method to use getMappedRange
		verticesBuffer.map(RotatingCubeScene.cubeVertexArray)
		verticesBuffer.unmap()

		renderPipeline = device.createRenderPipeline(
			RenderPipelineDescriptor(
				vertex = RenderPipelineDescriptor.VertexState(
					module = device.createShaderModule(
						ShaderModuleDescriptor(
							code = RotatingCubeScene.vertex
						)
					).bind(), // bind to autoClosableContext to release it later
					buffers = arrayOf(
						RenderPipelineDescriptor.VertexState.VertexBufferLayout(
							arrayStride = RotatingCubeScene.cubeVertexSize,
							attributes = arrayOf(
								RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
									shaderLocation = 0,
									offset = RotatingCubeScene.cubePositionOffset,
									format = "float32x4"
								),
								RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
									shaderLocation = 1,
									offset = RotatingCubeScene.cubeUVOffset,
									format = "float32x2"
								)
							)
						)
					)
				),
				fragment = RenderPipelineDescriptor.FragmentState(
					module = device.createShaderModule(
						ShaderModuleDescriptor(
							code = RotatingCubeScene.fragment
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

		val matrixSize = 4L * 16L; // 4x4 matrix
		val uniformBufferSize = offset + matrixSize;
		uniformBuffer = device.createBuffer(
			BufferDescriptor(
				size = uniformBufferSize,
				usage = BufferUsage.uniform or BufferUsage.copydst
			)
		).bind()

		uniformBindGroup1 = device.createBindGroup(
			BindGroupDescriptor(
				layout = renderPipeline.getBindGroupLayout(0),
				entries = arrayOf(
					BindGroupDescriptor.BindGroupEntry(
						binding = 0,
						resource = BindGroupDescriptor.BindGroupEntry.BufferBinding(
							buffer = uniformBuffer
						)
					)
				)
			)
		)

		uniformBindGroup2 = device.createBindGroup(
			BindGroupDescriptor(
				layout = renderPipeline.getBindGroupLayout(0),
				entries = arrayOf(
					BindGroupDescriptor.BindGroupEntry(
						binding = 0,
						resource = BindGroupDescriptor.BindGroupEntry.BufferBinding(
							buffer = uniformBuffer,
							offset = offset
						)
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
		projectionMatrix1 = Matrix4.perspective(fox, aspect, 1.0, 100.0)
			.translated(-2.0, 0.0, -7.0)
		projectionMatrix2 = Matrix4.perspective(fox, aspect, 1.0, 100.0)
			.translated(2.0, 0.0, -7.0)
	}

	override fun Application.render() = autoClosableContext {

		val transformationMatrix1 = RotatingCubeScene.getTransformationMatrix(
			frame / 100.0,
			projectionMatrix1
		)
		val transformationMatrix2 = RotatingCubeScene.getTransformationMatrix(
			frame / 100.0,
			projectionMatrix2
		)
		device.queue.writeBuffer(
			uniformBuffer,
			0,
			transformationMatrix1,
			0,
			transformationMatrix1.size.toLong()
		)
		device.queue.writeBuffer(
			uniformBuffer,
			offset,
			transformationMatrix2,
			0,
			transformationMatrix2.size.toLong()
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
		renderPassEncoder.setBindGroup(0, uniformBindGroup1)
		renderPassEncoder.setVertexBuffer(0, verticesBuffer)

		// Bind the bind group (with the transformation matrix) for
		// each cube, and draw.
		renderPassEncoder.setBindGroup(0, uniformBindGroup1);
		renderPassEncoder.draw(cubeVertexCount);

		renderPassEncoder.setBindGroup(0, uniformBindGroup2);
		renderPassEncoder.draw(cubeVertexCount);

		renderPassEncoder.end()
		val commandBuffer = encoder.finish()
			.bind()

		device.queue.submit(arrayOf(commandBuffer))

		renderingContext.present()

	}

	override fun close() {
		autoClosableContext.close()
	}

}