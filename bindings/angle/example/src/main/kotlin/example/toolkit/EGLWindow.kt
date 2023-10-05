package example.toolkit

import libangle.*

class EGLWindow {
}

data class ConfigParameters(
	var redBits: Int = -1,
	var greenBits: Int = -1,
	var blueBits: Int = -1,
	var alphaBits: Int = -1,
	var depthBits: Int = -1,
	var stencilBits: Int = -1,
	var webGLCompatibility: Boolean? = null,
	var robustResourceInit:  Boolean? = null,
	var componentType: EGLenum = EGL_COLOR_COMPONENT_TYPE_FIXED_EXT,
	var multisample: Boolean = false,
	var debug: Boolean = false,
	var noError: Boolean = false,
	var extensionsEnabled:  Boolean? = null,
	var bindGeneratesResource: Boolean = false,
	var clientArraysEnabled: Boolean = false,
	var robustAccess: Boolean = false,
	var mutableRenderBuffer: Boolean = false,
	var samples: EGLint = 0,
	var contextProgramCacheEnabled:  Boolean? = null,
	var resetStrategy: EGLenum = EGL_NO_RESET_NOTIFICATION_EXT,
	var colorSpace: EGLenum = EGL_COLORSPACE_LINEAR,
	var swapInterval: EGLint = 0
)