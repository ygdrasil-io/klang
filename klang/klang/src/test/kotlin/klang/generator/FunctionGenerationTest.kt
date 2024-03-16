package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeEnumeration
import klang.domain.NotBlankString
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toFunctionsSpec
import klang.mapper.toInterfaceSpec
import klang.parser.TestData

class FunctionGenerationTest : FreeSpec({

	val functions = TestData.functions.map { it.copy() }

	InMemoryDeclarationRepository().apply {
		NativeEnumeration(NotBlankString("EnumName"))
			.also { save(it) }
		functions.forEach { save(it) }
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin interface functions" {
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
			|    myEnum: kotlin.Int,
			|  ): kotlin.Byte
			|
			|  public fun function2(): com.sun.jna.Pointer?
			|
			|  public fun function3(): com.sun.jna.Pointer?
			|}
			|
		""".trimMargin()
	}

	"generate kotlin functions" {
		functions.toFunctionsSpec("", "Library").joinToString("\n") shouldBe """
			|/**
			| * @param a mapped from int *
			| * @param b mapped from void *
			| * @param myEnum mapped from enum EnumName
			| */
			|public fun function(
			|  a: com.sun.jna.Pointer?,
			|  b: com.sun.jna.Pointer?,
			|  myEnum: kotlin.Int,
			|): kotlin.Byte = Library.function(a, b, myEnum)
			|
			|public fun function2(): com.sun.jna.Pointer? = Library.function2()
			|
			|public fun function3(): com.sun.jna.Pointer? = Library.function3()
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