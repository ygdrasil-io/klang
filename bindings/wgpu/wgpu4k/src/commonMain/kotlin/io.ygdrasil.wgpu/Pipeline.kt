package io.ygdrasil.wgpu

expect class PipelineLayout

expect class RenderPipeline

class PipelineLayoutDescriptor
data class RenderPipelineDescriptor(
	var vertex: VertexState,
	var label: String? = null,
	var layout: PipelineLayout? = null,
	var primitive: PrimitiveState? = null,
	var depthStencil: DepthStencilState? = null,
	var fragment: FragmentState? = null,
	var multisample: MultisampleState? = null,
) {

	data class VertexState(
		var module: ShaderModule,
		var entryPoint: String? = null,
		var constants: Map<String, GPUPipelineConstantValue>? = null,
		var buffers: Array<VertexBufferLayout?>? = null,
	) {
		data class VertexBufferLayout(
			var arrayStride: GPUSize64,
			var attributes: Array<VertexAttribute> = arrayOf(),
			var stepMode: VertexStepMode? = null,
			/* "vertex" | "instance" */
		) {
			data class VertexAttribute(
				var format: String,
				/* "uint8x2" | "uint8x4" | "sint8x2" | "sint8x4" | "unorm8x2" | "unorm8x4" | "snorm8x2" | "snorm8x4" | "uint16x2" | "uint16x4" | "sint16x2" | "sint16x4" | "unorm16x2" | "unorm16x4" | "snorm16x2" | "snorm16x4" | "float16x2" | "float16x4" | "float32" | "float32x2" | "float32x3" | "float32x4" | "uint32" | "uint32x2" | "uint32x3" | "uint32x4" | "sint32" | "sint32x2" | "sint32x3" | "sint32x4" | "unorm10-10-10-2" */
				var offset: GPUSize64,
				var shaderLocation: GPUIndex32,
			)

		}
	}


	data class PrimitiveState(
		var topology: PrimitiveTopology? = null,
		/* "point-list" | "line-list" | "line-strip" | "triangle-list" | "triangle-strip" */
		var stripIndexFormat: String? = null,
		/* "uint16" | "uint32" */
		var frontFace: String? = null,
		/* "ccw" | "cw" */
		var cullMode: String? = null,
		/* "none" | "front" | "back" */
		var unclippedDepth: Boolean? = null,
	)

	data class DepthStencilState(
		var format: TextureFormat,
		/* "r8unorm" | "r8snorm" | "r8uint" | "r8sint" | "r16uint" | "r16sint" | "r16float" | "rg8unorm" | "rg8snorm" | "rg8uint" | "rg8sint" | "r32uint" | "r32sint" | "r32float" | "rg16uint" | "rg16sint" | "rg16float" | "rgba8unorm" | "rgba8unorm-srgb" | "rgba8snorm" | "rgba8uint" | "rgba8sint" | "bgra8unorm" | "bgra8unorm-srgb" | "rgb9e5ufloat" | "rgb10a2uint" | "rgb10a2unorm" | "rg11b10ufloat" | "rg32uint" | "rg32sint" | "rg32float" | "rgba16uint" | "rgba16sint" | "rgba16float" | "rgba32uint" | "rgba32sint" | "rgba32float" | "stencil8" | "depth16unorm" | "depth24plus" | "depth24plus-stencil8" | "depth32float" | "depth32float-stencil8" | "bc1-rgba-unorm" | "bc1-rgba-unorm-srgb" | "bc2-rgba-unorm" | "bc2-rgba-unorm-srgb" | "bc3-rgba-unorm" | "bc3-rgba-unorm-srgb" | "bc4-r-unorm" | "bc4-r-snorm" | "bc5-rg-unorm" | "bc5-rg-snorm" | "bc6h-rgb-ufloat" | "bc6h-rgb-float" | "bc7-rgba-unorm" | "bc7-rgba-unorm-srgb" | "etc2-rgb8unorm" | "etc2-rgb8unorm-srgb" | "etc2-rgb8a1unorm" | "etc2-rgb8a1unorm-srgb" | "etc2-rgba8unorm" | "etc2-rgba8unorm-srgb" | "eac-r11unorm" | "eac-r11snorm" | "eac-rg11unorm" | "eac-rg11snorm" | "astc-4x4-unorm" | "astc-4x4-unorm-srgb" | "astc-5x4-unorm" | "astc-5x4-unorm-srgb" | "astc-5x5-unorm" | "astc-5x5-unorm-srgb" | "astc-6x5-unorm" | "astc-6x5-unorm-srgb" | "astc-6x6-unorm" | "astc-6x6-unorm-srgb" | "astc-8x5-unorm" | "astc-8x5-unorm-srgb" | "astc-8x6-unorm" | "astc-8x6-unorm-srgb" | "astc-8x8-unorm" | "astc-8x8-unorm-srgb" | "astc-10x5-unorm" | "astc-10x5-unorm-srgb" | "astc-10x6-unorm" | "astc-10x6-unorm-srgb" | "astc-10x8-unorm" | "astc-10x8-unorm-srgb" | "astc-10x10-unorm" | "astc-10x10-unorm-srgb" | "astc-12x10-unorm" | "astc-12x10-unorm-srgb" | "astc-12x12-unorm" | "astc-12x12-unorm-srgb" */
		var depthWriteEnabled: Boolean? = null,
		var depthCompare: String? = null,
		/* "never" | "less" | "equal" | "less-equal" | "greater" | "not-equal" | "greater-equal" | "always" */

		var stencilFront: StencilFaceState? = null,
		var stencilBack: StencilFaceState? = null,
		var stencilReadMask: GPUStencilValue? = null,
		var stencilWriteMask: GPUStencilValue? = null,
		var depthBias: GPUDepthBias? = null,
		var depthBiasSlopeScale: Float? = null,
		var depthBiasClamp: Float? = null,
	) {
		data class StencilFaceState(
			var compare: String? = null,
			/* "never" | "less" | "equal" | "less-equal" | "greater" | "not-equal" | "greater-equal" | "always" */
			var failOp: String? = null,
			/* "keep" | "zero" | "replace" | "invert" | "increment-clamp" | "decrement-clamp" | "increment-wrap" | "decrement-wrap" */
			var depthFailOp: String? = null,
			/* "keep" | "zero" | "replace" | "invert" | "increment-clamp" | "decrement-clamp" | "increment-wrap" | "decrement-wrap" */
			var passOp: String? = null,
			/* "keep" | "zero" | "replace" | "invert" | "increment-clamp" | "decrement-clamp" | "increment-wrap" | "decrement-wrap" */
		)
	}


	data class MultisampleState(
		var count: GPUSize32? = null,
		var mask: GPUSampleMask? = null,
		var alphaToCoverageEnabled: Boolean? = null
	)

	data class FragmentState(
		var module: ShaderModule,
		var targets: Array<ColorTargetState?> = arrayOf(),
		var entryPoint: String? = null
	) {

		data class ColorTargetState(
			var format: TextureFormat,
			var writeMask: ColorWriteMask? = null,
			var blend: BlendState? = null
		) {
			data class BlendState(
				var color: BlendComponent,
				var alpha: BlendComponent
			) {
				data class BlendComponent(
					var operation: String? = null,
					/* "add" | "subtract" | "reverse-subtract" | "min" | "max" */
					var srcFactor: String? = null,
					/* "zero" | "one" | "src" | "one-minus-src" | "src-alpha" | "one-minus-src-alpha" | "dst" | "one-minus-dst" | "dst-alpha" | "one-minus-dst-alpha" | "src-alpha-saturated" | "constant" | "one-minus-constant" */
					var dstFactor: String? = null
					/* "zero" | "one" | "src" | "one-minus-src" | "src-alpha" | "one-minus-src-alpha" | "dst" | "one-minus-dst" | "dst-alpha" | "one-minus-dst-alpha" | "src-alpha-saturated" | "constant" | "one-minus-constant" */

				)
			}
		}
	}
}