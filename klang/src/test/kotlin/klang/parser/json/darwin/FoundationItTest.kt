package klang.parser.json.darwin

import io.kotest.core.spec.style.FreeSpec
import klang.DeclarationRepository
import klang.domain.NameableDeclaration
import klang.parser.json.parseAstJson

val IS_OS_DARWIN = System.getProperty("os.name").contains("mac", ignoreCase = true)
val INTEGRATION_ENABLED = System.getenv("integration.test").equals("enabled", ignoreCase = true)

class FoundationItTest : FreeSpec({

	"test foundation parsing".config(enabled = IS_OS_DARWIN && INTEGRATION_ENABLED) {

		// Given
		val filePath = "src/integrationTest/objective-c/foundation.m.ast.json"

		// When
		parseAstJson(filePath)

		with(DeclarationRepository) {

			resolve()

			declarations
				.asSequence()
				.filterIsInstance<NameableDeclaration>()
				.filter { it.name.startsWith("NS") }
				.forEach { println(it) }
		}

	}

})