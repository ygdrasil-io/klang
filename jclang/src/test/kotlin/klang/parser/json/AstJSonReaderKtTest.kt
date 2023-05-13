package klang.parser.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parseAstJson

class AstJSonReaderTest : StringSpec({

	val enumerations = listOf(
		"EnumName" to listOf("Value1" to 0x2, "Value2" to 0x1),
		"EnumNameWithoutExplicitValues" to listOf(
			"EnumNameWithoutExplicitValues_Value1" to 0,
			"EnumNameWithoutExplicitValues_Value2" to 1
		)
	)

	val structures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "struct StructName", "field2" to "struct StructName *", "field3" to "char")
	)

	val typeDefStructures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "StructName", "field2" to "StructName *", "field3" to "char")
	)

	"test enum parsing" {
		// Given
		val filePath = "sample/c/enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		enumerations.forEach { (name, values) ->
			DeclarationRepository.findNativeEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}

	"test typedef enum parsing" {
		// Given
		val filePath = "sample/c/typedef-enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		enumerations.forEach { (name, values) ->
			DeclarationRepository.findNativeEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}

	"test struct parsing" {
		// Given
		val filePath = "sample/c/struct.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		structures.forEach { (name, fields) ->
			DeclarationRepository.findNativeStructureByName(name)
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
			DeclarationRepository.findNativeStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
		}
	}


	"function parsing" {
		// Given
		val filePath = "sample/c/functions.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		DeclarationRepository.findNativeFunctionByName("function")
			.also { it?.name shouldBe "function" }
			.also { it?.returnType shouldBe "void" }
			.also {
				it?.arguments shouldBe listOf(
					"a" to "int *",
					"b" to "void *",
					"enum" to "EnumName"
				).map { type -> type to "arg_$type" }
			}
	}

	beforeTest {
		DeclarationRepository.clear()
		ParserRepository.errors.clear()
	}

	afterTest {
		ParserRepository.errors.size shouldBe 0
	}
})