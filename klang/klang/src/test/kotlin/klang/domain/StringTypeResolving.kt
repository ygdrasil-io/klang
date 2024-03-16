package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter
import klang.parser.testType

class StringTypeResolving : FreeSpec({

	val nativeTypeAlias = NativeTypeAlias(
		name = NotBlankString("MyString"),
		typeRef = testType("char *")
	)

	InMemoryDeclarationRepository().also { repository ->
		repository.save(nativeTypeAlias)
		repository.resolveTypes(allDeclarationsFilter)
	}

	"should resolve string type as primitive" {
		nativeTypeAlias.typeRef.let { typeRef ->
			typeRef::class shouldBe ResolvedTypeRef::class
			(typeRef as ResolvedTypeRef).type::class shouldBe StringType::class
		}
	}
})