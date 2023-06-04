package klang.parser.ast

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RawAstPasserTest : StringSpec({

	val hierarchyByRawAstUnit = listOf(
		"|-Test" to 1,
		"|  -Test" to 2,
		"|    -Test" to 3,
	)

	hierarchyByRawAstUnit.forEach { (rawAstUnit, expectedHierarchy) ->
		"test hierarchy position of $rawAstUnit" {
			rawAstUnit.getPositionOnHierarchy() shouldBe expectedHierarchy
		}
	}

})