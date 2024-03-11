@file:OptIn(ExperimentalStdlibApi::class)

package io.ygdrasil.wgpu

expect class Sampler : AutoCloseable {
}


class SamplerDescriptor(
	var addressModeU: String? = null, /* "clamp-to-edge" | "repeat" | "mirror-repeat" */
	var addressModeV: String? = null, /* "clamp-to-edge" | "repeat" | "mirror-repeat" */
	var addressModeW: String? = null, /* "clamp-to-edge" | "repeat" | "mirror-repeat" */
	var magFilter: String? = null, /* "nearest" | "linear" */

	var minFilter: String? = null, /* "nearest" | "linear" */
	var mipmapFilter: String? = null,/* "nearest" | "linear" */
	var lodMinClamp: Number? = null,
	var lodMaxClamp: Number? = null,
	var compare: String? = null, /* "never" | "less" | "equal" | "less-equal" | "greater" | "not-equal" | "greater-equal" | "always" */
	var maxAnisotropy: Number? = null,
	var label: String? = null,
)