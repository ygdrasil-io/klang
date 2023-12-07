package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toInterfaceSpec
import klang.parser.TestData

class FunctionGenerationTest : FreeSpec({

	val functions = TestData.functions.map { it.copy() }

	InMemoryDeclarationRepository().apply {
		functions.forEach { save(it) }
		resolveTypes()
	}

	"generate kotlin functions" {
		functions.toInterfaceSpec("", "Interface").toString() shouldBe """
			|public interface Interface : com.sun.jna.Library {
			|  /**
			|   * @param a mapped from int *
			|   * @param b mapped from void *
			|   * @param myEnum mapped from enum EnumName
			|   */
			|  public fun function(
			|    a: com.sun.jna.Pointer?,
			|    b: com.sun.jna.Pointer?,
			|    myEnum: EnumName?,
			|  ): kotlin.Byte
			|
			|  public fun function2(): com.sun.jna.Pointer
			|}
			|
		""".trimMargin()
	}

	"generate kotlin functions library" {
		generateInterfaceLibrarySpec("Interface", "Library", "Name").toString() shouldBe """
			|val libLibrary: Interface.Library by lazy { klang.internal.NativeLoad<Interface.Library>("Name") }
			|
		""".trimMargin()
	}
})