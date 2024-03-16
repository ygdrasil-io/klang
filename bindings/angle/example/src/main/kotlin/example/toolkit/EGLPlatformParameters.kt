package example.toolkit

import libangle.*
import java.util.*

class PlatformMethods

class EGLPlatformParameters {
	var renderer: Int = EGL_PLATFORM_ANGLE_TYPE_DEFAULT_ANGLE
	var majorVersion: Int = EGL_DONT_CARE.toInt()
	var minorVersion: Int = EGL_DONT_CARE.toInt()
	var deviceType: Int = EGL_PLATFORM_ANGLE_DEVICE_TYPE_HARDWARE_ANGLE
	var presentPath: Int = EGL_DONT_CARE.toInt()
	var debugLayersEnabled: Int = EGL_DONT_CARE.toInt()
	var robustness: Int = EGL_DONT_CARE.toInt()
	var displayPowerPreference: Int = EGL_DONT_CARE.toInt()
	var enabledFeatureOverrides: MutableList<Feature> = ArrayList()
	var disabledFeatureOverrides: MutableList<Feature> = ArrayList()
	var platformMethods: PlatformMethods? = null
	constructor() // Constructor por defecto
	constructor(renderer: Int) {
		this.renderer = renderer
	}

	constructor(renderer: Int, majorVersion: Int, minorVersion: Int, deviceType: Int) {
		this.renderer = renderer
		this.majorVersion = majorVersion
		this.minorVersion = minorVersion
		this.deviceType = deviceType
	}

	constructor(renderer: Int, majorVersion: Int, minorVersion: Int, deviceType: Int, presentPath: Int) {
		this.renderer = renderer
		this.majorVersion = majorVersion
		this.minorVersion = minorVersion
		this.deviceType = deviceType
		this.presentPath = presentPath
	}

	fun tie(): List<Any?> {
		return listOf(renderer, majorVersion, minorVersion, deviceType, presentPath, debugLayersEnabled, robustness, displayPowerPreference, disabledFeatureOverrides, enabledFeatureOverrides, platformMethods)
	}

	fun enable(feature: Feature): EGLPlatformParameters {
		enabledFeatureOverrides.add(feature)
		return this
	}

	fun disable(feature: Feature): EGLPlatformParameters {
		disabledFeatureOverrides.add(feature)
		return this
	}
}
