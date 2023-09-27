package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import klang.parser.TestData
import klang.parser.testType

class CallbackGenerationTest : FreeSpec({

	val callback = NativeTypeAlias(
		name = "MyCallback",
		typeRef = testType(TestData.basicFunctionPointer),
	)

	InMemoryDeclarationRepository().apply {
		save(callback)
		resolveTypes()
	}

	"generate kotlin callback" {
		callback.toSpec("test").toString() shouldBe """
			|public interface MyCallback : com.sun.jna.Callback {
			|  public operator fun invoke(
			|    param1: com.sun.jna.Pointer,
			|    param2: kotlin.String,
			|    param3: kotlin.Int,
			|  )
			|}
			|
			""".trimMargin()
	}
})