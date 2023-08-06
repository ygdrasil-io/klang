package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.KotlinEnumeration

class EnumerationGenerationTest : FreeSpec({

	val enumeration = KotlinEnumeration(
		name = "MyEnum",
		type = "Long",
		values = listOf(
			Pair("FIRST", "1"),
			Pair("SECOND", "2"),
			Pair("THIRD", "3")
		)
	)

	"generate kotlin enumeration" {
		enumeration.generateCode() shouldBe """
enum class MyEnum(val value: Long) {
	FIRST(1),
	SECOND(2),
	THIRD(3);

	companion object {
		fun of(value: Long): MyEnum? = entries.find { it.value == value }
	}
}
		""".trimIndent()
	}

	"generate kotlin enumeration v2" {
		enumeration.generateCode2().toString() shouldBe """
public enum class MyEnum(
  public val nativeValue: Long,
) {
  FIRST(1),
  SECOND(2),
  THIRD(3),
  ;

  public companion object {
    public fun of(nativeValue: Long): MyEnum? = entries.find { it.nativeValue == nativeValue }
  }
}

		""".trimIndent()
	}
})

