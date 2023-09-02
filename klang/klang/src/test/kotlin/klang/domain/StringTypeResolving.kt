package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.parser.testType

class StringTypeResolving : FreeSpec({

	val nativeTypeAlias = NativeTypeAlias(
		name = "MyString",
		type = testType("char *")
	)

	InMemoryDeclarationRepository().also { repository ->
		repository.save(nativeTypeAlias)
		repository.resolveTypes()
	}

	"should resolve string type as primitive" {
		nativeTypeAlias.type.let { typeRef ->
			typeRef::class shouldBe ResolvedTypeRef::class
			(typeRef as ResolvedTypeRef).type::class shouldBe StringType::class
		}
	}
})