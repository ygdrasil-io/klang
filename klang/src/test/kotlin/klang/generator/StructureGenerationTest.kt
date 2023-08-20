package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NativeStructure
import klang.mapper.toSpec
import klang.parser.testType

class StructureGenerationTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"first" to testType("Long"),
			"second" to testType("Int"),
		)
	)

	"generate kotlin structure" {
		structure.toSpec("test").toString() shouldBe """
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