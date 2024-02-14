package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeEnumeration
import klang.domain.NotBlankString
import klang.mapper.toSpecAsEnumeration

class EnumerationGenerationTest : FreeSpec({

	val enumeration = NativeEnumeration(
		name = NotBlankString("MyEnum"),
		values = listOf(
			Pair("FIRST", 1),
			Pair("SECOND", 2),
			Pair("THIRD", 3)
		)
	)

	InMemoryDeclarationRepository().apply {
		save(enumeration)
		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin enumeration" {
		enumeration.toSpecAsEnumeration("mypackage").toString() shouldBe """
			|public enum class MyEnum(
			|  public val `value`: kotlin.Int,
			|) {
			|  FIRST(1),
			|  SECOND(2),
			|  THIRD(3),
			|  ;
			|
			|  public infix fun or(other: kotlin.Int): kotlin.Int = value or other
			|
			|  public infix fun or(other: mypackage.MyEnum): kotlin.Int = value or other.value
			|
			|  public companion object {
			|    public fun of(`value`: kotlin.Int): mypackage.MyEnum? = entries.find {
			|      it.value == value 
			|    }
			|  }
			|}
			|
		""".trimMargin()
	}
})

