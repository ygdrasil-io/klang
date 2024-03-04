package io.ygdrasil.wgpu.examples

import io.ygdrasil.wgpu.PipelineLayoutDescriptor
import io.ygdrasil.wgpu.RenderPipelineDescriptor
import io.ygdrasil.wgpu.ShaderModuleDescriptor

class SimpleTriangleScene : Application.Scene() {
	override fun Application.initialiaze() {
		val shaderModule = device.createShaderModule(
			ShaderModuleDescriptor(
				code = shader
			)
		)

		val pipelineLayout = device.createPipelineLayout(PipelineLayoutDescriptor())

		val renderPipeline = device.createRenderPipeline(
			RenderPipelineDescriptor().apply {
				layout = pipelineLayout
			}
		)

		TODO("Not yet implemented")
	}

	override fun Application.render() {
		TODO("Not yet implemented")
	}
}

private val shader = """
	@vertex
	fn vs_main(@builtin(vertex_index) in_vertex_index: u32) -> @builtin(position) vec4<f32> {
	    let x = f32(i32(in_vertex_index) - 1);
	    let y = f32(i32(in_vertex_index & 1u) * 2 - 1);
	    return vec4<f32>(x, y, 0.0, 1.0);
	}

	@fragment
	fn fs_main() -> @location(0) vec4<f32> {
	    return vec4<f32>(1.0, 0.0, 0.0, 1.0);
	}
""".trimIndent()