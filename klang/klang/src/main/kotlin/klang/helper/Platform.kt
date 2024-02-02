import java.util.*

/**
 * Represents an operating system.
 */
enum class OperatingSystem {
	LINUX, WINDOWS, MAC, UNKNOWN
}

/**
 * Lazily initializes and retrieves the operating system on which the code is running.
 * The operating system is inferred from the value of the `os.name` system property.
 * Supported operating systems are WINDOWS, LINUX, MAC, and UNKNOWN.
 *
 * @return The inferred operating system.
 */
val operatingSystem by lazy { inferOperatingSystem() }

private fun inferOperatingSystem(): OperatingSystem {
	val os = System.getProperty("os.name").lowercase(Locale.getDefault())
	return when {
		os.contains("win") -> OperatingSystem.WINDOWS
		os.contains("nix") || os.contains("nux") -> OperatingSystem.LINUX
		os.contains("mac") -> OperatingSystem.MAC
		else -> OperatingSystem.UNKNOWN
	}
}