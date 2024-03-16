package klang.mapper

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NativeEnumeration
import klang.domain.NotBlankString

class EnumerationMapperTest : FreeSpec({

	val enumeration = NativeEnumeration(
		name = NotBlankString("MyEnum"),
		values = listOf(
			"FIRST" to 1,
			"SECOND" to 2,
			"THIRD" to 3
		)
	)

	"generate kotlin enumeration specifications" {
		enumeration.toSpecAsEnumeration("mypackage").apply {
			name shouldBe enumeration.name.value
			enumConstants.size shouldBe enumeration.values.size
			enumConstants.map { it.key } shouldBe enumeration.values.map { it.first }
		}
	}
})

