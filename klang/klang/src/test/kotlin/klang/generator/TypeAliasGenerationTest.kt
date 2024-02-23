package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeTypeAlias
import klang.domain.NotBlankString
import klang.mapper.toSpec
import klang.parser.TestData
import klang.parser.testType

class TypeAliasGenerationTest : FreeSpec({

	"test type alias generation with void *" {

		TestData.typeDef[0].toSpec("klang").first()
			.toString() shouldBe """
				|public typealias NewType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}

	"test type alias generation with OldStructureType *" {

		TestData.typeDef[1].toSpec("klang").first()
			.toString() shouldBe """
				|public typealias NewStructureType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}

	"test type alias generation with primitive type" {
		val alias = NativeTypeAlias(
			name = NotBlankString("NewType"),
			typeRef = testType("int")
		)

		InMemoryDeclarationRepository().apply {
			save(alias)
			resolveTypes(allDeclarationsFilter)
		}

		alias.toSpec("klang").joinToString("") shouldBe """
				|public typealias NewType = kotlin.Int
				|public typealias `NewType${'$'}Array` = kotlin.IntArray
				|
				""".trimMargin()
	}

})