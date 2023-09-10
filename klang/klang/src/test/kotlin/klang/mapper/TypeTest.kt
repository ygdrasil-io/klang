package klang.mapper

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.domain.NativeStructure
import klang.domain.NativeTypeAlias
import klang.parser.TestData
import klang.parser.testType

class TypeTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			"callback" to testType(TestData.basicFunctionPointer),
			"callback2" to testType("MyAlias"),
		)
	)

	val typeAlias = NativeTypeAlias(
		name = "MyAlias",
		typeRef = testType(TestData.basicFunctionPointer)
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		save(typeAlias)
		resolveTypes()
	}

	"toType" {
		structure.fields[0].second.toType("test") shouldBe jnaCallback
		structure.fields[1].second.toType("test") shouldBe jnaCallback

	}
})
