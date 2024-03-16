package klang.parser.json.darwin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NameableDeclaration
import klang.parser.INTEGRATION_ENABLED
import klang.parser.IS_OS_DARWIN
import klang.parser.json.ParserRepository
import klang.parser.json.parseAstJson

class CocoaItTest : FreeSpec({

	"test cocoa parsing".config(enabled = IS_OS_DARWIN && INTEGRATION_ENABLED) {

		// Given
		val filePath = "src/integrationTest/objective-c/cocoa.m.ast.json"

		// When
		with(parseAstJson(filePath)) {


			ParserRepository.errors shouldBe emptyList()

			resolveTypes()

			declarations
				.asSequence()
				.filterIsInstance<NameableDeclaration>()
				.filter { it.name.value == "NSWindow" }
				.forEach { println(it) }
		}

	}

})