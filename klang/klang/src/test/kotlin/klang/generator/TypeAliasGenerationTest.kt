package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.mapper.toSpec
import klang.parser.TestData

class TypeAliasGenerationTest : FreeSpec({

	"test type alias generation with void *" {

		TestData.typeDef[0].toSpec("klang")
			.toString() shouldBe """
				|public typealias NewType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}

	"test type alias generation with OldStructureType *" {

		TestData.typeDef[1].toSpec("klang")
			.toString() shouldBe """
				|public typealias NewStructureType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}
})