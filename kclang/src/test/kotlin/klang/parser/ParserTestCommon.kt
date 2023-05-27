package klang.parser

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import klang.DeclarationRepository
import klang.parser.json.ParserRepository

@Ignored
open class ParserTestCommon(body: StringSpec.() -> Unit = {}) : StringSpec({

	beforeTest {
		DeclarationRepository.clear()
		ParserRepository.errors.clear()
	}

	afterTest {
		ParserRepository.errors.size shouldBe 0
	}

	body()
})

fun validateEnumerations(enumerations: List<Pair<String, List<Pair<String, Long>>>>) {
	enumerations.forEach { (name, values) ->
		DeclarationRepository.findEnumerationByName(name)
			.also { it?.name shouldBe name }
			.also { it?.values shouldBe values }
	}
}
