package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeFunction
import klang.domain.NativeStructure
import klang.domain.NotBlankString
import klang.domain.TypeRefField
import klang.mapper.toFunctionsSpec
import klang.mapper.toInterfaceSpec
import klang.parser.testType

class FunctionGenerationWithStructurePointerTest : FreeSpec({


	val structure = NativeStructure(
		name = NotBlankString("MyStructure"),
		fields = listOf(
			TypeRefField("field1", testType("int")),
			TypeRefField("field2", testType("char")),
		)
	)

	val function = NativeFunction(
			name = NotBlankString("function"),
			returnType = testType("void"),
			arguments = listOf(
				NativeFunction.Argument(NotBlankString("structure"), testType("MyStructure *")),
			)
		)

	InMemoryDeclarationRepository().apply {
		save(function)
		save(structure)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin interface functions" {
		listOf(function).toInterfaceSpec("test", "Interface").toString() shouldBe """
			|public interface Interface : com.sun.jna.Library {
			|  /**
			|   * @param structure mapped from MyStructure *
			|   */
			|  public fun function(structure: test.MyStructure?)
			|}
			|
		""".trimMargin()
	}

	"generate kotlin functions" {
		listOf(function).toFunctionsSpec("test", "Interface").joinToString() shouldBe """
			|/**
			| * @param structure mapped from MyStructure *
			| */
			|public fun function(structure: test.MyStructure?): kotlin.Unit = Interface.function(structure)
			|
		""".trimMargin()
	}

})