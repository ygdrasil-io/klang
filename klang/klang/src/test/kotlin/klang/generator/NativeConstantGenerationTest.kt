package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NativeConstant
import klang.domain.NotBlankString
import klang.mapper.toSpec

class NativeConstantGenerationTest : FreeSpec({

	"generate kotlin string constant" {
		NativeConstant(NotBlankString("CONSTANT"), "1").toSpec("test").apply {
			toString() shouldBe """
      			|val CONSTANT: kotlin.String = "1"
      			|
		""".trimMargin()
		}
	}

	"generate kotlin long constant" {
		NativeConstant(NotBlankString("CONSTANT"), 1L).toSpec("test").apply {
			toString() shouldBe """
      			|val CONSTANT: kotlin.Long = 1L
      			|
		""".trimMargin()
		}

		NativeConstant(NotBlankString("CONSTANT"), Long.MAX_VALUE).toSpec("test").apply {
			toString() shouldBe """
      			|val CONSTANT: kotlin.Long = Long.MAX_VALUE
      			|
		""".trimMargin()
		}

		NativeConstant(NotBlankString("CONSTANT"), Long.MIN_VALUE).toSpec("test").apply {
			toString() shouldBe """
      			|val CONSTANT: kotlin.Long = Long.MIN_VALUE
      			|
		""".trimMargin()
		}
	}

	"generate kotlin double constant" {
		NativeConstant(NotBlankString("CONSTANT"), 1.1).toSpec("test").apply {
			toString() shouldBe """
      			|val CONSTANT: kotlin.Double = 1.1
      			|
		""".trimMargin()
		}
	}


})