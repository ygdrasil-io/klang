package klang.mapper

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.domain.NativeEnumeration

class EnumerationMapperTest : FreeSpec({

	val enumeration = NativeEnumeration(
		name = "MyEnum",
		values = listOf(
			"FIRST" to 1,
			"SECOND" to 2,
			"THIRD" to 3
		)
	)

	"generate kotlin enumeration specifications" {
		enumeration.toSpec().apply {
			name shouldBe enumeration.name
			enumConstants.size shouldBe enumeration.values.size
			enumConstants.map { it.key } shouldBe enumeration.values.map { it.first }
		}
	}
})

