package klang.parser

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.domain.NativeStructure
import klang.parser.json.ParserRepository

@Ignored
open class ParserTestCommon(body: FreeSpec.() -> Unit = {}) : FreeSpec({

	beforeContainer {
		ParserRepository.errors.clear()
	}

	beforeTest {
		ParserRepository.errors.size shouldBe 0
	}

	body()
})

 suspend fun FreeSpecContainerScope.validateEnumerations(repository: DeclarationRepository, enumerations: List<Pair<String, List<Pair<String, Long>>>>) {
	enumerations.forEach { (name, values) ->
		"test $name" {
			repository.findEnumerationByName(name)
				.also { it?.name shouldBe name }
				.also { it?.values shouldBe values }
		}
	}
}

suspend fun FreeSpecContainerScope.validateStructures(repository: DeclarationRepository, structures: List<NativeStructure>) {
	structures.forEach { (name, fields, isUnion) ->
		"test $name" {
			repository.findStructureByName(name)
				.also { it?.name shouldBe name }
				.also { it?.fields shouldBe fields }
				.also { it?.isUnion shouldBe isUnion }
		}
	}
}
