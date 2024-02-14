package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NotBlankString
import klang.domain.ObjectiveCClass
import klang.domain.typeOf
import klang.mapper.toSpec

class ObjectiveCGenerationTest : FreeSpec({

	val objectiveC = ObjectiveCClass(
		name = NotBlankString("MyObjectiveCClass"),
		superType = typeOf("NSObject").getOrNull(),
		protocols = setOf(),
		properties = listOf(),
		methods = listOf(),
		categories = setOf()

	)

	"generate kotlin objectiveC" {
		objectiveC.toSpec().toString() shouldBe """
public class MyObjectiveCClass(
  id: kotlin.Long,
) : darwin.NSObject(id)

""".trimIndent()
	}
})