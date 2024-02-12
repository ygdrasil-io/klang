package klang.parser.libclang

import OperatingSystem
import klang.domain.NameableDeclaration
import klang.helper.unzipFromClasspath
import klang.parser.ParserTestCommon
import mu.KotlinLogging
import operatingSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString

private val logger = KotlinLogging.logger {}
private val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

class SDL2ItTest : ParserTestCommon({

	"test SDL2 parsing" {

		// Given
		val (tempDirectory, otherHeaderTempDirectoryPath) = initSDL2HeaderDirectory()
		val fileToParse = "SDL2/SDL.h"
		val filePath = tempDirectory.absolutePathString()
		val headerPaths = when (operatingSystem) {
			OperatingSystem.MAC -> arrayOf(
				otherHeaderTempDirectoryPath.resolve("c").absolutePathString(),
				otherHeaderTempDirectoryPath.resolve("darwin-headers").absolutePathString(),
			)
			else -> arrayOf(
				otherHeaderTempDirectoryPath.resolve("c").absolutePathString()
			)
		}


		// When
		val repository = parseFile(fileToParse, filePath, headerPaths)
			.also {
				it.resolveTypes { resolvableDeclaration ->
					when (resolvableDeclaration) {
						is NameableDeclaration -> resolvableDeclaration.name.startsWith("_").not()
						else -> true
					}
				}
			}

		// Then
		repository.apply {
			println(declarations.size)
		}

	}
})

private fun initSDL2HeaderDirectory(): Pair<Path, Path> {
	val tempDirectoryPath = Files.createTempDirectory("SDL2")
		.also { it.toFile().deleteOnExit() }
	val otherHeaderTempDirectoryPath = Files.createTempDirectory("headers")
		.also { it.toFile().deleteOnExit() }

	logger.info { "will use directory ${tempDirectoryPath.absolutePathString()} to parse SDL2" }

	val sdl2HeadersFile = "/SDL2-headers-${inferPlatformSuffix()}.zip"
	unzipFromClasspath(sdl2HeadersFile, tempDirectoryPath.toFile())

	val cHeadersFile = "/c-${inferPlatformSuffix()}-headers.zip"
	unzipFromClasspath(cHeadersFile, otherHeaderTempDirectoryPath.toFile())

	if (operatingSystem == OperatingSystem.MAC) {
		val darwinHeaders = "/darwin-headers.zip"
		unzipFromClasspath(darwinHeaders, otherHeaderTempDirectoryPath.toFile())
	}

	return tempDirectoryPath to otherHeaderTempDirectoryPath
}

private fun inferPlatformSuffix() = when (operatingSystem) {
	OperatingSystem.MAC -> "darwin"
	OperatingSystem.LINUX -> "linux"
	else -> error("Operating system $operatingSystem not supported")
}