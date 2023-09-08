package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import klang.parser.TestData
import klang.parser.TestData.basicFunctionPointer
import klang.parser.testType

class TypeAliasGenerationTest : FreeSpec({

	"test type alias generation with void *" {

		TestData.typeDef[0].toSpec("klang", InMemoryDeclarationRepository())
			.toString() shouldBe """
				|public typealias NewType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}

	"test type alias generation with OldStructureType *" {

		TestData.typeDef[1].toSpec("klang", InMemoryDeclarationRepository())
			.toString() shouldBe """
				|public typealias NewStructureType = com.sun.jna.Pointer
				|
				""".trimMargin()

	}

	val typeAliasWithCallback = NativeTypeAlias(
		name = "NewType",
		type = testType(basicFunctionPointer)
	)

	val repository = InMemoryDeclarationRepository().apply {
		save(typeAliasWithCallback)
		resolveTypes()
	}

	"test type alias generation with callback" {

		typeAliasWithCallback.toSpec("klang", repository)
			.toString() shouldBe """
				|public typealias NewType = com.sun.jna.Callback
				|
				""".trimMargin()

	}
})