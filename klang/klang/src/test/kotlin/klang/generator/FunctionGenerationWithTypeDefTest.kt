package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.domain.NativeFunction
import klang.domain.NativeTypeAlias
import klang.domain.NotBlankString
import klang.domain.typeOf
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toFunctionsSpec
import klang.mapper.toInterfaceSpec
import klang.parser.TestData
import klang.parser.testType

class FunctionGenerationWithTypeDefTest : FreeSpec({

	val function = NativeFunction(
		NotBlankString("function"),
		testType("MyType"),
		listOf(NativeFunction.Argument(NotBlankString("a"), testType("MyType")))
	)

	InMemoryDeclarationRepository().apply {

		NativeTypeAlias(NotBlankString("MyType"), testType("void *"))
			.also { save((it)) }

		save(function)

		resolveTypes(allDeclarationsFilter)
	}

	"generate kotlin interface functions" {
		listOf(function).toFunctionsSpec("", "libInterface").first().toString() shouldBe """
			|/**
			| * @param a mapped from MyType
			| */
			|public fun function(a: MyType?): MyType? = libInterface.function(a)
			|
		""".trimMargin()
	}

})