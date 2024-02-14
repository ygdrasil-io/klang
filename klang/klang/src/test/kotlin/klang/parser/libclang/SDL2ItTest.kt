package klang.parser.libclang

import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.domain.NativeEnumeration
import klang.helper.HeaderManager
import klang.helper.HeaderManager.inferPlatformSuffix
import klang.helper.unzipFromClasspath
import klang.parser.ParserTestCommon
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

private val logger = KotlinLogging.logger {}

class SDL2ItTest : ParserTestCommon({

	"test SDL2 parsing" {

		// Given
		val (tempDirectory, otherHeaderTempDirectoryPath) = initSDL2HeaderDirectory()
		val fileToParse = "SDL2/SDL.h"
		val filePath = tempDirectory.absolutePathString()
		val headerPaths = HeaderManager.listPlatformHeadersFromPath(otherHeaderTempDirectoryPath)

		// When
		val repository = parseFile(fileToParse, filePath, headerPaths)
			.also(DeclarationRepository::resolveTypes)

		repository.findStructureByName("SDL_Rect")
			.let { println("SDL_Rect $it") }

		// Then
		repository.apply {
			val libraryDeclarations = findLibraryDeclaration()
			println(libraryDeclarations.size)

			libraryDeclarations.filterIsInstance<NativeEnumeration>()
				.forEach {
					logger.info("testing ${it.name} enumeration")
					it.name.value.isNotBlank() shouldNotBe true
					it.values.isEmpty() shouldNotBe true
				}
		}

	}
})

private fun initSDL2HeaderDirectory(): Pair<Path, Path> {
	val tempDirectoryPath = Files.createTempDirectory("SDL2")
		.also { it.toFile().deleteOnExit() }
	val otherHeaderTempDirectoryPath = Files.createTempDirectory("headers")
		.also { it.toFile().deleteOnExit() }

	logger.info { "will use directory ${tempDirectoryPath.absolutePathString()} to parse SDL2" }

	HeaderManager.putPlatformHeaderAt(otherHeaderTempDirectoryPath)

	val sdl2HeadersFile = "/SDL2-headers-${inferPlatformSuffix()}.zip"
	unzipFromClasspath(sdl2HeadersFile, tempDirectoryPath.toFile())

	return tempDirectoryPath to otherHeaderTempDirectoryPath
}
