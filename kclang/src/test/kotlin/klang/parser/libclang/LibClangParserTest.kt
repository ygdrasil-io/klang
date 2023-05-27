package klang.parser.libclang

import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations

class LibClangParserTest : ParserTestCommon({

	"test enum parsing" {
		// Given
		val filePath = "sample/c/enum.h"

		// When
		parseFile(filePath)

		// Then
		validateEnumerations(TestData.enumerations)
	}

	"test typedef enum parsing" {
		// Given
		val filePath = "sample/c/typedef-enum.h"

		// When
		parseFile(filePath)

		// Then
		validateEnumerations(TestData.enumerations)

	}
})