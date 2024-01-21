package klang.parser.libclang

import klang.parser.INTEGRATION_ENABLED
import klang.parser.IS_OS_DARWIN
import klang.parser.ParserTestCommon


class SDL2ItTest : ParserTestCommon({

	"test SDL2 parsing".config(enabled = INTEGRATION_ENABLED || true) {

		// Given
		val filePath = "src/test/c/"
		val fileToParse = "SDL2/SDL.h"
		val headerPaths = arrayOf("src/test/c/c/include")

		// When
		val repository = parseFile(fileToParse, filePath, headerPaths)

		// Then
		repository.apply {
			println(declarations.size)
		}


	}
})