package klang.mapper

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.InMemoryDeclarationRepository
import klang.domain.*
import klang.parser.TestData
import klang.parser.testType

class TypeTest : FreeSpec({

	val structure = NativeStructure(
		name = "MyStructure",
		fields = listOf(
			TypeRefField("callback", testType(TestData.basicFunctionPointer)),
			TypeRefField("callback2", testType("MyAlias")),
		)
	)

	val typeAlias = NativeTypeAlias(
		name = "MyAlias",
		typeRef = testType(TestData.basicFunctionPointer)
	)

	val primitiveArrayTypeAlias = NativeTypeAlias(
		name = "MyAliasWithPrimitiveArray",
		typeRef = testType("int[10]")
	)

	InMemoryDeclarationRepository().apply {
		save(structure)
		save(typeAlias)
		save(primitiveArrayTypeAlias)
		resolveTypes()
	}

	"toType" {
		(structure.fields[0] as TypeRefField).type.toType("test") shouldBe jnaCallback
		(structure.fields[1]as TypeRefField).type.toType("test") shouldBe jnaCallback
		primitiveArrayTypeAlias.typeRef
			.let { it as? ResolvedTypeRef }
			.also { it shouldNotBe null }
			?.also {
				(it.type is FixeSizeType) shouldBe true
				it.isArray shouldBe true
				it.arraySize shouldBe 10
			}

	}
})
