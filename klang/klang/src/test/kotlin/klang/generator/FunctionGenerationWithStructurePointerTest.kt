package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeFunction
import klang.domain.NativeStructure
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toInterfaceSpec
import klang.parser.TestData
import klang.parser.testType

class FunctionGenerationWithStructurePointerTest : FreeSpec({


	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"field1" to testType("int"),
			"field2" to testType("char"),
		)
	)

	val function = NativeFunction(
			name = "function",
			returnType = testType("void"),
			arguments = listOf(
				NativeFunction.Argument("structure", testType("MyStructure *")),
			)
		)

	InMemoryDeclarationRepository().apply {
		save(function)
		save(structure)
		resolveTypes()
	}

	"generate kotlin functions" {
		listOf(function).toInterfaceSpec("test", "Interface").toString() shouldBe """
			|public interface Interface : com.sun.jna.Library {
			|  /**
			|   * @param structure mapped from MyStructure *
			|   */
			|  public fun function(structure: test.MyStructure)
			|}
			|
		""".trimMargin()
	}

})