package darwin

import com.sun.jna.Library
import darwin.internal.NativeLoad

private interface CoreGraphics : Library {
	fun MTLCreateSystemDefaultDevice(): Long
	companion object : CoreGraphics by NativeLoad("/System/Library/Frameworks/CoreGraphics.framework/Versions/A/Resources/BridgeSupport/CoreGraphics.dylib")
}


fun MTLCreateSystemDefaultDevice(): MTLDevice? = CoreGraphics.MTLCreateSystemDefaultDevice()
	.takeIf { it != 0L }
	?.let { MTLDevice(it) }