package klang.parser.json

import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations

class AstJSonReaderTest : ParserTestCommon({

	val structures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "struct StructName", "field2" to "struct StructName *", "field3" to "char")
	)

	val typeDefStructures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "StructName", "field2" to "StructName *", "field3" to "char")
	)

	val typeDef = listOf(
		"NewType" to "void *",
		"NewStructureType" to "struct OldStructureType *"
	)

	"test enum parsing" {
		// Given
		val filePath = "sample/c/enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateEnumerations(TestData.enumerations)
	}

	"test typedef enum parsing" {
		// Given
		val filePath = "sample/c/typedef-enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateEnumerations(TestData.enumerations)

	}

	"test struct parsing" {
		// Given
		val filePath = "sample/c/struct.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		structures.forEach { (name, fields) ->
			DeclarationRepository.findStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
		}

	}

	"typedef struct parsing" {
		// Given
		val filePath = "sample/c/typedef-struct.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		typeDefStructures.forEach { (name, fields) ->
			DeclarationRepository.findStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
		}
	}

	"typedef parsing" {
		// Given
		val filePath = "sample/c/typedef.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		typeDef.forEach { (name, type) ->
			DeclarationRepository.findTypeAliasByName(name)
					.also { it?.name shouldBe name }
					.also { it?.type shouldBe type }
		}
	}

	"function parsing" {
		// Given
		val filePath = "sample/c/functions.h.ast.json"

		// When
		parseAstJson(filePath)

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


