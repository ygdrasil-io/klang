package klang.parser.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parseAstJson

class AstJSonReaderTest: StringSpec({

	beforeTest {
		DeclarationRepository.clear()
		ParserRepository.errors.clear()
	}

	val enumerations = listOf(
		"EnumName" to listOf("Value1" to 0x2, "Value2" to 0x1),
		"EnumNameWithoutExplicitValues" to listOf("EnumNameWithoutExplicitValues_Value1" to 0, "EnumNameWithoutExplicitValues_Value2" to 1)
	)

	val structures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "struct StructName", "field2" to "struct StructName *", "field3" to "char")
	)

	"test enum parsing" {
		// Given
		val filePath = "sample/c/enum.h.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		ParserRepository.errors.size shouldBe 0
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
		ParserRepository.errors.size shouldBe 0
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
		ParserRepository.errors.size shouldBe 0
		structures.forEach { (name, fields) ->
			DeclarationRepository.findNativeStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
		}

	}
})