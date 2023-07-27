package klang.parser.json

import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations
import klang.parser.validateStructures

class AstJSonCParserTest : ParserTestCommon({

	"test enum parsing" - {
		// Given
		val filePath = "src/test/c/enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateEnumerations(TestData.enumerations)
	}

	"test typedef enum parsing" - {
		// Given
		val filePath = "src/test/c/typedef-enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateEnumerations(TestData.enumerations)

	}

	"test struct parsing" - {
		// Given
		val filePath = "src/test/c/struct.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateStructures(TestData.structures)
	}

	"typedef struct parsing" - {
		// Given
		val filePath = "src/test/c/typedef-struct.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateStructures(TestData.typeDefStructures)
	}

	"typedef parsing" - {
		// Given
		val filePath = "src/test/c/typedef.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		TestData.typeDef.forEach { (name, type) ->
			"test $name" {
				DeclarationRepository.findTypeAliasByName(name)
					.also { it?.name shouldBe name }
					.also { it?.type shouldBe type }
			}
		}
	}

	"function parsing" - {
		// Given
		val filePath = "src/test/c/functions.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		"test function" {
			DeclarationRepository.findFunctionByName("function")
				.also { it?.name shouldBe "function" }
				.also { it?.returnType shouldBe "char" }
				.also {
					it?.arguments
						?.map { (name, type) -> name to type } shouldBe listOf(
						"a" to "int *",
						"b" to "void *",
						"myEnum" to "enum EnumName"
					)
				}
		}

		"test function" {
			DeclarationRepository.findFunctionByName("function2")
				.also { it?.name shouldBe "function2" }
				.also { it?.returnType shouldBe "void *" }
				.also { it?.arguments shouldBe listOf() }
		}
	}

})


