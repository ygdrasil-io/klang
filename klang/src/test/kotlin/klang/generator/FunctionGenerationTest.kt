package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toInterfaceSpec
import klang.parser.TestData

class FunctionGenerationTest : FreeSpec({

	"generate kotlin functions" {
		TestData.functions.toInterfaceSpec("", "Interface").toString() shouldBe """
			|public interface Interface {
			|  public fun function(
			|    a: com.sun.jna.Pointer,
			|    b: com.sun.jna.Pointer,
			|    myEnum: EnumName,
			|  ): kotlin.Byte
			|
			|  public fun function2(): com.sun.jna.Pointer
			|}
			|
		""".trimMargin()
	}

	"generate kotlin functions library" {
		generateInterfaceLibrarySpec("Interface", "Library").toString() shouldBe """
			|val Interface by lazy { darwin.`internal`.NativeLoad<Interface>("Library") }
			|
		""".trimMargin()
	}
})