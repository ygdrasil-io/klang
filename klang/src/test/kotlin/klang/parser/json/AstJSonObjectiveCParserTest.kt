package klang.parser.json

import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.domain.NameableDeclaration
import klang.domain.ObjectiveCClass
import klang.domain.ObjectiveCProtocol
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

	"test protocol parsing" - {

		// Given
		val filePath = "sample/objective-c/protocol.m.ast.json"

		// When
		parseAstJson(filePath)

		// Then
		validateObjectiveCProtocol(TestData.objectiveCProtocol)
	}
})
suspend fun FreeSpecContainerScope.validateObjectiveCProtocol(objectiveCClasses: List<ObjectiveCProtocol>) {
	objectiveCClasses.forEach { objectiveCClass ->
		"test ${objectiveCClass.name}" {
			DeclarationRepository.findObjectiveCProtocolByName(objectiveCClass.name)
				.also { it?.name shouldBe objectiveCClass.name }
				.also { it?.protocols shouldBe objectiveCClass.protocols }
				.also { it?.properties shouldContains objectiveCClass.properties }
				.also { it?.methods shouldContains objectiveCClass.methods }
		}
	}
}
suspend fun FreeSpecContainerScope.validateObjectiveCClass(objectiveCClasses: List<ObjectiveCClass>) {
	objectiveCClasses.forEach { objectiveCClass ->
		"test ${objectiveCClass.name}" {
		DeclarationRepository.findObjectiveCClassByName(objectiveCClass.name)
			.also { it?.name shouldBe objectiveCClass.name }
			.also { it?.superType shouldBe objectiveCClass.superType }
			.also { it?.protocols shouldBe objectiveCClass.protocols }
			.also { it?.properties shouldContains objectiveCClass.properties }
			.also { it?.methods shouldContains objectiveCClass.methods }
		}
	}
}

private infix fun <E : NameableDeclaration> List<E>?.shouldContains(properties: List<NameableDeclaration>) {
	this shouldNotBe null
	shouldContainExactly(properties)
}
