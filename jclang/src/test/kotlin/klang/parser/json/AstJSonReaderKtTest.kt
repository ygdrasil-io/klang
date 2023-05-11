package klang.parser.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.parseAstJson
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class AstJSonReaderTest: StringSpec({

	val values = listOf(
		"EnumName" to listOf("Value1" to 0x2, "Value2" to 0x1),
		"EnumNameWithoutExplicitValues" to listOf("EnumNameWithoutExplicitValues_Value1" to 0, "EnumNameWithoutExplicitValues_Value2" to 1)
	)

	"test enum parsing" {
		// Given
		val filePath = "sample/c/enum.h.ast.json"

		DeclarationRepository.clear()

		// When
		parseAstJson(filePath)

		// Then
		values.forEach { (name, values) ->
			DeclarationRepository.findNativeEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}

	"test typedef enum parsing" {
		// Given
		val filePath = "sample/c/typedef-enum.h.ast.json"
		DeclarationRepository.clear()

		// When
		parseAstJson(filePath)

		// Then
		values.forEach { (name, values) ->
			DeclarationRepository.findNativeEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}
})