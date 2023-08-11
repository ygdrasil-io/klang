package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.KotlinEnumeration
import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import klang.mapper.toSpec

class StructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"first" to "Long",
			"second" to "Int",
		)

	)

	"generate kotlin structure" {
		structure.toSpec().toString() shouldBe """
@com.sun.jna.Structure.FieldOrder("first", "second")
public open class MyStructure(
  pointer: com.sun.jna.Pointer? = null,
) : com.sun.jna.Structure(pointer) {
  @JvmField
  public var first: Long = 0

  @JvmField
  public var second: Int = 0

  public class ByReference(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByReference

  public class ByValue(
    pointer: com.sun.jna.Pointer? = null,
  ) : MyStructure(pointer), com.sun.jna.Structure.ByValue
}

		""".trimIndent()
	}
})