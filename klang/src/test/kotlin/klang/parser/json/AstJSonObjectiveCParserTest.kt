package klang.parser.json

import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.domain.NameableDeclaration
import klang.domain.ObjectiveCClass
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations

class AstJSonObjectiveCParserTest : ParserTestCommon({

	"test class parsing" - {

		// Given
		val filePath = "sample/objective-c/class.m.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateObjectiveCClass(TestData.objectiveCClass)
	}

	"test enum parsing" - {

		// Given
		val filePath = "sample/objective-c/nsenum.m.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateEnumerations(TestData.objectiveCEnumeration)
	}
})

suspend fun FreeSpecContainerScope.validateObjectiveCClass(objectiveCClasses: List<Pair<String, List<NameableDeclaration>>>) {
	objectiveCClasses.forEach { (className, properties) ->
		"test $className" {
		DeclarationRepository.findObjectiveCClassByName(className)
			.also { it?.name shouldBe className }
			.also { it?.properties shouldContains properties.filterIsInstance<ObjectiveCClass.Property>() }
			.also { it?.methods shouldContains properties.filterIsInstance<ObjectiveCClass.Method>() }
		}
	}
}

private infix fun <E : NameableDeclaration> List<E>?.shouldContains(properties: List<NameableDeclaration>) {
	this shouldNotBe null
	shouldContainExactly(properties)
}