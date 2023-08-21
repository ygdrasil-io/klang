package klang.generator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.*
import klang.mapper.toSpec

class ObjectiveCGenerationTest : FreeSpec({

	val objectiveC = ObjectiveCClass(
		name = "MyObjectiveCClass",
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