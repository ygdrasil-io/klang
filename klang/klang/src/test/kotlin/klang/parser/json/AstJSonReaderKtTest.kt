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
		val repository = parseAstJson(filePath)

		// Then
		validateEnumerations(repository, TestData.enumerations)
	}

	"test typedef enum parsing" - {
		// Given
		val filePath = "src/test/c/typedef-enum.h.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateEnumerations(repository, TestData.enumerations)

	}

	"test struct parsing" - {
		// Given
		val filePath = "src/test/c/struct.h.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateStructures(repository, TestData.structures)
	}

	"typedef struct parsing" - {
		// Given
		val filePath = "src/test/c/typedef-struct.h.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateStructures(repository, TestData.typeDefStructures)
	}

	"typedef parsing" - {
		// Given
		val filePath = "src/test/c/typedef.h.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		TestData.typeDef.forEach { (name, type) ->
			"test $name" {
				repository.findTypeAliasByName(name)
					.also { it?.name shouldBe name }
					.also { it?.type shouldBe type }
			}
		}
	}

	"function parsing" - {
		// Given
		val filePath = "src/test/c/functions.h.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		TestData
			.functions
			.forEach { function ->
				"test function with name ${function.name}" {
					repository.findFunctionByName(function.name)
						.also { it?.name shouldBe function.name}
						.also { it?.returnType shouldBe function.returnType }
						.also { it?.arguments shouldBe function.arguments }
				}
			}
	}

})


