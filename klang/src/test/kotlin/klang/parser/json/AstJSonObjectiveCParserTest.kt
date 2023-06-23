package klang.parser.json

import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.ParserTestCommon
import klang.parser.TestData

class AstJSonObjectiveCParserTest : ParserTestCommon({

	"test class parsing" {

		// Given
		val filePath = "sample/objective-c/class.m.ast.json"

		// When
		parseAstJson(filePath, isObjectiveC = true)

		// Then
		validateObjectiveCClass(TestData.objectiveCClass)
	}
})

fun validateObjectiveCClass(objectiveCClasses: List<Pair<String, Map<String, String>>>) {
	objectiveCClasses.forEach { (className, members) ->
		DeclarationRepository.findObjectiveCClassByName(className)
			.also { it?.name shouldBe className }
	}
}
