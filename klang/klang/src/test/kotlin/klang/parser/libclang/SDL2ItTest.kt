package klang.parser.libclang

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.helper.HeaderManager
import klang.helper.HeaderManager.inferPlatformSuffix
import klang.helper.unzipFromClasspath
import klang.parser.ParserTestCommon
import mu.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.absolutePathString

private val logger = KotlinLogging.logger {}

class SDL2ItTest : ParserTestCommon({

	"test SDL2 parsing" {

		// Given
		val (tempDirectory, otherHeaderTempDirectoryPath) = initSDL2HeaderDirectory()
		val SDL2header = "SDL2/SDL.h"
		val SDL2OpenglESheader = "SDL2/SDL_opengles2.h"
		val filePath = tempDirectory.absolutePathString()
		val headerPaths = HeaderManager.listPlatformHeadersFromPath(otherHeaderTempDirectoryPath)

		// When
		val repository = InMemoryDeclarationRepository()
			.parseFile(SDL2header, filePath, headerPaths)
			.parseFile(SDL2OpenglESheader, filePath, headerPaths, mapOf("SDL_USE_BUILTIN_OPENGL_DEFINITIONS" to "1"))
			// And
			.also(DeclarationRepository::resolveTypes)

		// Then
		repository.apply {
			val libraryDeclarations = findLibraryDeclaration()

			libraryDeclarations.filterIsInstance<NativeEnumeration>()
				.forEach {
					logger.info("testing ${it.name} enumeration")
					it.name.value.isNotBlank() shouldBe true
					it.values.isEmpty() shouldNotBe true
				}

			findFunctionByName("SDL_ReportAssertion") shouldNotBe null
			findFunctionByName("glGetString") shouldNotBe null
			findStructureByName("SDL_Rect") shouldNotBe null
		}

	}
})

private fun initSDL2HeaderDirectory(): Pair<Path, Path> {
	val tempDirectoryPath = HeaderManager.createTemporaryHeaderDirectory("SDL2")
	val otherHeaderTempDirectoryPath = HeaderManager.createTemporaryHeaderDirectory("headers")

	logger.info { "will use directory ${tempDirectoryPath.absolutePathString()} to parse SDL2" }

	HeaderManager.putPlatformHeaderAt(otherHeaderTempDirectoryPath)

	val sdl2HeadersFile = "/SDL2-headers-${inferPlatformSuffix()}.zip"
	unzipFromClasspath(sdl2HeadersFile, tempDirectoryPath.toFile())

	return tempDirectoryPath to otherHeaderTempDirectoryPath
}
