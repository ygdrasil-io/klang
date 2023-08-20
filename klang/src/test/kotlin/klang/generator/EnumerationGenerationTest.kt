package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NativeEnumeration
import klang.mapper.toSpec

class EnumerationGenerationTest : FreeSpec({

	val enumeration = NativeEnumeration(
		name = "MyEnum",
		values = listOf(
			Pair("FIRST", 1),
			Pair("SECOND", 2),
			Pair("THIRD", 3)
		)
	)

	"generate kotlin enumeration" {
		enumeration.toSpec().toString() shouldBe """
public enum class MyEnum(
  public val nativeValue: kotlin.Long,
) {
  FIRST(1),
  SECOND(2),
  THIRD(3),
  ;

  public companion object {
    public fun of(nativeValue: kotlin.Long): MyEnum? = entries.find { it.nativeValue == nativeValue }
  }
}

		""".trimIndent()
	}
})

