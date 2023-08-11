package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.*
import klang.mapper.toInterfaceSpec
import klang.mapper.toSpec
import klang.parser.TestData

class FunctionGenerationTest : FreeSpec({

	"generate kotlin functions" {
		TestData.functions.toInterfaceSpec("Interface").toString() shouldBe """
public interface Interface {
  public fun function(
    a: Pointer,
    b: Pointer,
    myEnum: EnumName,
  ): char

  public fun function2(): Pointer 
}

""".trimIndent()
	}
})