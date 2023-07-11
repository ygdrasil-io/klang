package klang.parser.json

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.domain.NameableDeclaration
import klang.domain.ObjectiveCClass
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

fun validateObjectiveCClass(objectiveCClasses: List<Pair<String, List<ObjectiveCClass.Property>>>) {
	objectiveCClasses.forEach { (className, properties) ->
		DeclarationRepository.findObjectiveCClassByName(className)
			.also { it?.name shouldBe className }
			.also { it?.properties shouldContains properties }
	}
}

private infix fun <E : NameableDeclaration> List<E>?.shouldContains(properties: List<NameableDeclaration>) {
	this shouldNotBe null
	shouldContainExactly(properties)
}
