package klang.parser.libclang

import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations
import klang.parser.validateStructures

class LibClangParserTest : ParserTestCommon({

	"test enum parsing" - {
		// Given
		val filePath = "sample/c/enum.h"

		// When
		parseFile(filePath)

		// Then
		validateEnumerations(TestData.enumerations)
	}

	"test typedef enum parsing" - {
		// Given
		val filePath = "sample/c/typedef-enum.h"

		// When
		parseFile(filePath)

		// Then
		validateEnumerations(TestData.enumerations)

	}

	"test struct parsing" - {
		// Given
		val filePath = "sample/c/struct.h"

		// When
		parseFile(filePath)

		// Then
		validateStructures(TestData.structures)
	}

	"typedef struct parsing" - {
		// Given
		val filePath = "sample/c/typedef-struct.h"

		// When
		parseFile(filePath)

		// Then
		validateStructures(TestData.typeDefStructures)
	}

	"typedef parsing" {
		// Given
		val filePath = "sample/c/typedef.h"

		// When
		parseFile(filePath)

		// Then
		TestData.typeDef.forEach { (name, type) ->
			DeclarationRepository.findTypeAliasByName(name)
				.also { it?.name shouldBe name }
				.also { it?.type shouldBe type }
		}
	}

	"function parsing" {
		// Given
		val filePath = "sample/c/functions.h"

		// When
		parseFile(filePath)

		// Then
		DeclarationRepository.findFunctionByName("function")
			.also { it?.name shouldBe "function" }
			.also { it?.returnType shouldBe "char" }
			.also {
				it?.arguments
					?.map { (name, type) -> name to type }shouldBe listOf(
					"a" to "int *",
					"b" to "void *",
					"myEnum" to "enum EnumName"
				)
			}

		DeclarationRepository.findFunctionByName("function2")
			.also { it?.name shouldBe "function2" }
			.also { it?.returnType shouldBe "void *" }
			.also { it?.arguments shouldBe listOf() }
	}
})