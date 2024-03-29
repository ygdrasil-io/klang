package klang.parser.json

import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.DeclarationRepository
import klang.domain.NameableDeclaration
import klang.domain.ObjectiveCCategory
import klang.domain.ObjectiveCClass
import klang.domain.ObjectiveCProtocol
import klang.parser.ParserTestCommon
import klang.parser.TestData
import klang.parser.validateEnumerations

class AstJSonObjectiveCParserTest : ParserTestCommon({

	"test class parsing" - {

		// Given
		val filePath = "src/test/objective-c/class.m.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateObjectiveCClass(repository, TestData.objectiveCClass)
	}

	"test categories parsing" - {

		// Given
		val filePath = "src/test/objective-c/category.m.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateObjectiveCCategory(repository, TestData.objectiveCCategory)
	}

	"test enum parsing" - {

		// Given
		val filePath = "src/test/objective-c/nsenum.m.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateEnumerations(repository, TestData.objectiveCEnumeration)
	}

	"test protocol parsing" - {

		// Given
		val filePath = "src/test/objective-c/protocol.m.ast.json"

		// When
		val repository = parseAstJson(filePath)

		// Then
		validateObjectiveCProtocol(repository, TestData.objectiveCProtocol)
	}
})

suspend fun FreeSpecContainerScope.validateObjectiveCCategory(repository: DeclarationRepository, objectiveCCategories: List<ObjectiveCCategory>) {
	objectiveCCategories.forEach { objectiveCCategory ->
		"test ${objectiveCCategory.name}" {
			repository.findObjectiveCCategoryByName(objectiveCCategory.name)
				.also { it?.name shouldBe objectiveCCategory.name }
				.also { it?.superType shouldBe objectiveCCategory.superType }
				.also { it?.methods shouldContains objectiveCCategory.methods }
		}
	}
}

suspend fun FreeSpecContainerScope.validateObjectiveCProtocol(repository: DeclarationRepository, objectiveCClasses: List<ObjectiveCProtocol>) {
	objectiveCClasses.forEach { objectiveCClass ->
		"test ${objectiveCClass.name}" {
			repository.findObjectiveCProtocolByName(objectiveCClass.name)
				.also { it?.name shouldBe objectiveCClass.name }
				.also { it?.protocols shouldBe objectiveCClass.protocols }
				.also { it?.properties shouldContains objectiveCClass.properties }
				.also { it?.methods shouldContains objectiveCClass.methods }
		}
	}
}
suspend fun FreeSpecContainerScope.validateObjectiveCClass(repository: DeclarationRepository, objectiveCClasses: List<ObjectiveCClass>) {
	objectiveCClasses.forEach { objectiveCClass ->
		"test ${objectiveCClass.name}" {
			repository.findObjectiveCClassByName(objectiveCClass.name)
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
