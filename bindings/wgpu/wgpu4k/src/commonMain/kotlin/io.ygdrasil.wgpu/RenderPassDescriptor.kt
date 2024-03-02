package io.ygdrasil.wgpu

// TODO to implement
class GPURenderPassDepthStencilAttachment

// TODO to implement
class GPUQuerySet

// TODO to implement
class GPURenderPassTimestampWrites

data class RenderPassDescriptor(
	var colorAttachments: Array<ColorAttachment> = arrayOf(),
	var depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null,
	var occlusionQuerySet: GPUQuerySet? = null,
	var timestampWrites: GPURenderPassTimestampWrites? = null,
	var maxDrawCount: GPUSize64? = null
) {

	data class ColorAttachment(
		var view: TextureView,
		var loadOp: String, /* "load" | "clear" */
        var storeOp: String, /* "store" | "discard" */
        var depthSlice: GPUIntegerCoordinate? = null,
		var resolveTarget: TextureView? = null,
		var clearValue: Array<Number>? = null,
    )
}