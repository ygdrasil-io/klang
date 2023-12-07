package example.toolkit

import libangle.*
import libgles.libGLESv2Library

class EGLWindow(window: OSWindow) {

	private lateinit var mContext: Any

	fun initializeGLWithResult(
		osWindow: OSWindow,
		glWindowingLibrary: Library,
		driverType: GLESDriverType,
		platformParams: EGLPlatformParameters,
		configParams: ConfigParameters
	): GLWindowResult {
		if (!initializeDisplay(osWindow, glWindowingLibrary, driverType, platformParams)) {
			return GLWindowResult.Error
		}

		val res = initializeSurface(osWindow, glWindowingLibrary, configParams)
		if (res != GLWindowResult.NoError) {
			return res
		}

		if (!initializeContext()) {
			return GLWindowResult.Error
		}

		return GLWindowResult.NoError
	}

	fun initializeDisplay(osWindow: OSWindow, glWindowingLibrary: Library, driverType: GLESDriverType, params: EGLPlatformParameters): Boolean {
		if (driverType == GLESDriverType.ZinkEGL) {
			val driDirStream = StringBuilder()
			val s = GetPathSeparator()
			driDirStream.append(GetModuleDirectory()).append("mesa").append(s).append("src").append(s).append("gallium").append(s).append("targets").append(s).append("dri")

			val driDir = driDirStream.toString()

			SetEnvironmentVar("MESA_LOADER_DRIVER_OVERRIDE", "zink")
			SetEnvironmentVar("LIBGL_DRIVERS_PATH", driDir)
		}

		if (ANGLE_USE_UTIL_LOADER) {
			var getProcAddress: PFNEGLGETPROCADDRESSPROC
			glWindowingLibrary.getAs("eglGetProcAddress", getProcAddress)
			if (getProcAddress == null) {
				println("Cannot load eglGetProcAddress")
				return false
			}

			// Likely we will need to use a fallback to Library::getAs on non-ANGLE platforms.
			LoadUtilEGL(getProcAddress)
		}

		// EGL_NO_DISPLAY + EGL_EXTENSIONS returns NULL before Android 10
		val extensionString = eglQueryString(EGL_NO_DISPLAY, EGL_EXTENSIONS) as String?
		if (extensionString == null) {
			// fallback to an empty string for strstr
			extensionString = ""
		}

		val displayAttributes = mutableListOf(
			EGL_PLATFORM_ANGLE_TYPE_ANGLE,
			params.renderer,
			EGL_PLATFORM_ANGLE_MAX_VERSION_MAJOR_ANGLE,
			params.majorVersion,
			EGL_PLATFORM_ANGLE_MAX_VERSION_MINOR_ANGLE,
			params.minorVersion
		)

		if (params.deviceType != EGL_DONT_CARE) {
			displayAttributes.addAll(listOf(
				EGL_PLATFORM_ANGLE_DEVICE_TYPE_ANGLE,
				params.deviceType
			))
		}

		if (params.presentPath != EGL_DONT_CARE) {
			if (extensionString.indexOf("EGL_ANGLE_experimental_present_path") == -1) {
				destroyGL()
				return false
			}

			displayAttributes.addAll(listOf(
				EGL_EXPERIMENTAL_PRESENT_PATH_ANGLE,
				params.presentPath
			))
		}

		// Set debug layer settings if requested.
		if (params.debugLayersEnabled != EGL_DONT_CARE) {
			displayAttributes.addAll(listOf(
				EGL_PLATFORM_ANGLE_DEBUG_LAYERS_ENABLED_ANGLE,
				params.debugLayersEnabled
			))
		}

		if (params.platformMethods != null) {
			displayAttributes.addAll(listOf(
				EGL_PLATFORM_ANGLE_PLATFORM_METHODS_ANGLEX,
				params.platformMethods.toLong()
			))
		}

		if (params.displayPowerPreference != EGL_DONT_CARE) {
			displayAttributes.addAll(listOf(
				EGL_POWER_PREFERENCE_ANGLE,
				params.displayPowerPreference
			))
		}

		val enabledFeatureOverrides = mutableListOf<String>()
		val disabledFeatureOverrides = mutableListOf<String>()

		for (feature in params.enabledFeatureOverrides) {
			enabledFeatureOverrides.add(GetFeatureName(feature))
		}
		for (feature in params.disabledFeatureOverrides) {
			disabledFeatureOverrides.add(GetFeatureName(feature))
		}

		val hasFeatureControlANGLE = extensionString.indexOf("EGL_ANGLE_feature_control") != -1

		if (!hasFeatureControlANGLE &&
			(!enabledFeatureOverrides.isEmpty() || !disabledFeatureOverrides.isEmpty())) {
			println("Missing EGL_ANGLE_feature_control.")
			destroyGL()
			return false
		}

		if (!disabledFeatureOverrides.isEmpty()) {
			disabledFeatureOverrides.add(null)

			displayAttributes.addAll(listOf(
				EGL_FEATURE_OVERRIDES_DISABLED_ANGLE,
				disabledFeatureOverrides.toLongArray()
			))
		}

		if (hasFeatureControlANGLE) {
			// Always enable exposeNonConformantExtensionsAndVersions in ANGLE tests.
			enabledFeatureOverrides.addAll(listOf(
				"exposeNonConformantExtensionsAndVersions",
				null
			))

			displayAttributes.addAll(listOf(
				EGL_FEATURE_OVERRIDES_ENABLED_ANGLE,
				enabledFeatureOverrides.toLongArray()
			))
		}

		displayAttributes.add(EGL_NONE)

		if (driverType == GLESDriverType.SystemWGL) {
			return false
		}

		val mDisplay: EGLDisplay
		if (IsANGLE(driverType) && extensionString.indexOf("EGL_ANGLE_platform_angle") != -1) {
			mDisplay = eglGetPlatformDisplay(
				EGL_PLATFORM_ANGLE_ANGLE,
				osWindow.getNativeDisplay().toLong(),
				displayAttributes.toTypedArray()
			)
		} else {
			mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)
		}

		if (mDisplay == EGL_NO_DISPLAY) {
			println("Failed to get display: 0x%X".format(eglGetError()))
			destroyGL()
			return false
		}

		val majorVersion = intArrayOf(0)
		val minorVersion = intArrayOf(0)
		if (eglInitialize(mDisplay, majorVersion, minorVersion) == EGL_FALSE) {
			println("eglInitialize failed: 0x%X".format(eglGetError()))
			destroyGL()
			return false
		}

		queryFeatures()

		mPlatform = params
		return true
	}

	fun initializeContext(): Boolean {
		mContext = createContext(EGL_NO_CONTEXT, null)
		if (mContext == EGL_NO_CONTEXT) {
			destroyGL()
			return false
		}

		if (!makeCurrent()) {
			destroyGL()
			return false
		}

		return true
	}
}

enum class GLESDriverType {
	AngleEGL,
	AngleVulkanSecondariesEGL,
	SystemEGL,
	SystemWGL,
	ZinkEGL,
}

enum class GLWindowResult {
	NoError,
	NoColorspaceSupport,
	NoMutableRenderBufferSupport,
	Error,
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