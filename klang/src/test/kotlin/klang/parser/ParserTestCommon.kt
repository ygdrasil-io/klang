package klang.parser

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.core.spec.style.scopes.FreeSpecTerminalScope
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.json.ParserRepository

@Ignored
open class ParserTestCommon(body: FreeSpec.() -> Unit = {}) : FreeSpec({

	beforeContainer {
		DeclarationRepository.clear()
		ParserRepository.errors.clear()
	}

	beforeTest {
		ParserRepository.errors.size shouldBe 0
	}

	body()
})

 suspend fun FreeSpecContainerScope.validateEnumerations(enumerations: List<Pair<String, List<Pair<String, Long>>>>) {
	enumerations.forEach { (name, values) ->
		"test $name" {
			DeclarationRepository.findEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}
}

suspend fun FreeSpecContainerScope.validateStructures(structures: List<Pair<String, List<Pair<String, String>>>>) {
	structures.forEach { (name, fields) ->
		"test $name" {
			DeclarationRepository.findStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
		}
	}
}
