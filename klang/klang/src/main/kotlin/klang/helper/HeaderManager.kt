package klang.helper

import OperatingSystem
import klang.domain.NotBlankString
import operatingSystem
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists

object HeaderManager {
	fun putPlatformHeaderAt(headerDirectoryPath: Path) {

		check(headerDirectoryPath.exists()) { "path ${headerDirectoryPath.absolutePathString()} does not exists" }

		val cHeadersFile = "/c-${inferPlatformSuffix()}-headers.zip"
		unzipFromClasspath(cHeadersFile, headerDirectoryPath.toFile())

		if (operatingSystem == OperatingSystem.MAC) {
			val darwinHeaders = "/darwin-headers.zip"
			unzipFromClasspath(darwinHeaders, headerDirectoryPath.toFile())
		}

	}

	fun listPlatformHeadersFromPath(headerDirectoryPath: Path) = when (operatingSystem) {
		OperatingSystem.MAC -> arrayOf(
			headerDirectoryPath.resolve("c").absolutePathString(),
			headerDirectoryPath.resolve("darwin-headers").absolutePathString(),
		)
		else -> arrayOf(
			headerDirectoryPath.resolve("c").absolutePathString()
		)
	}

	fun inferPlatformSuffix() = when (operatingSystem) {
		OperatingSystem.MAC -> "darwin"
		OperatingSystem.LINUX -> "linux"
		else -> error("Operating system $operatingSystem not supported")
	}

	fun createTemporaryHeaderDirectory(directoryName: String = "headers") = createTemporaryHeaderDirectory(NotBlankString(directoryName))

	@OptIn(ExperimentalPathApi::class)
	fun createTemporaryHeaderDirectory(directoryName: NotBlankString): Path = Files.createTempDirectory("headers")
			.also(::deleteDirectoryOnShutdown)
}

@OptIn(ExperimentalPathApi::class)
private fun deleteDirectoryOnShutdown(it: Path) {
	Runtime.getRuntime().addShutdownHook(object : Thread() {
		override fun run() {
			it.deleteRecursively()
		}
	})
}