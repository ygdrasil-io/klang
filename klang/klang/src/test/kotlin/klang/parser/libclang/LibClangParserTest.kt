package klang.parser.libclang

import io.kotest.matchers.shouldBe
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations
import klang.parser.validateStructures

class LibClangParserTest : ParserTestCommon({

	"test enum parsing" - {
		// Given
		val filePath = "src/test/c/enum.h"

		// When
		val repository = parseFile(filePath)

		// Then
		validateEnumerations(repository, TestData.enumerations)
	}

	"test typedef enum parsing" - {
		// Given
		val filePath = "src/test/c/typedef-enum.h"

		// When
		val repository = parseFile(filePath)

		// Then
		validateEnumerations(repository, TestData.enumerations)

	}

	"test struct parsing" - {
		// Given
		val filePath = "src/test/c/struct.h"

		// When
		val repository = parseFile(filePath)

		// Then
		validateStructures(repository, TestData.structures)
	}

	"typedef struct parsing" - {
		// Given
		val filePath = "src/test/c/typedef-struct.h"

		// When
		val repository = parseFile(filePath)

		// Then
		validateStructures(repository, TestData.typeDefStructures)
	}

	"typedef parsing" {
		// Given
		val filePath = "src/test/c/typedef.h"

		// When
		val repository = parseFile(filePath)

		// Then
		TestData.typeDef.forEach { (name, type) ->
			repository.findTypeAliasByName(name)
				.also { it?.name shouldBe name }
				.also { it?.typeRef shouldBe type }
		}
	}

	"function parsing" {
		// Given
		val filePath = "src/test/c/functions.h"

		// When
		val repository = parseFile(filePath)

		// Then
		repository.findFunctionByName("function")
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

		repository.findFunctionByName("function2")
			.also { it?.name shouldBe "function2" }
			.also { it?.returnType shouldBe "void *" }
			.also { it?.arguments shouldBe listOf() }
	}
})