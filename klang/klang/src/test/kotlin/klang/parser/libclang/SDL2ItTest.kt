package klang.parser.libclang

import klang.helper.unzipFromClasspath
import klang.parser.INTEGRATION_ENABLED
import klang.parser.IS_OS_DARWIN
import klang.parser.ParserTestCommon
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

private val logger = KotlinLogging.logger {}
private val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

class SDL2ItTest : ParserTestCommon({

	"test SDL2 parsing" {

		// Given
		val tempDirectory = initSDL2HeaderDirectory()
		val fileToParse = "SDL2/SDL.h"
		val filePath = tempDirectory.absolutePathString()
		val headerPaths = arrayOf(
			tempDirectory.resolve("c").resolve("include").absolutePathString()
		)

		// When
		val repository = parseFile(fileToParse, filePath, headerPaths)
			.also { it.resolveTypes() }

		// Then
		repository.apply {
			println(declarations.size)
		}


	}
})

private fun initSDL2HeaderDirectory(): Path {
	val tempDirectoryPath = Files.createTempDirectory("SDL2")
		.also { it.toFile().deleteOnExit() }

	logger.info { "will use directory ${tempDirectoryPath.absolutePathString()} to parse SDL2" }

	val sdl2HeadersFile = "/SDL2-headers-${inferPlatformSuffix()}.zip"
	val cHeadersFile = "/c-headers.zip"
	unzipFromClasspath(sdl2HeadersFile, tempDirectoryPath.toFile())
	unzipFromClasspath(cHeadersFile, tempDirectoryPath.toFile())

	return tempDirectoryPath
}

private fun inferPlatformSuffix() = when {
	osName.contains("mac") -> "darwin"
	osName.contains("linux") -> "linux"
	else -> error("OS $osName not supported")
}