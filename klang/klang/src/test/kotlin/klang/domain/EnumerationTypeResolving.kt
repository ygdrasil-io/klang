package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.InMemoryDeclarationRepository
import klang.allDeclarationsFilter

class EnumerationTypeResolving : FreeSpec({

	val enumeration = NativeEnumeration(
		name = NotBlankString("MyEnumeration"),
		values = listOf("first" to 1L)
	)

	InMemoryDeclarationRepository().also { repository ->
		repository.save(enumeration)
		repository.resolveTypes(allDeclarationsFilter)
	}

	"should resolve enumeration type as primitive" {
		enumeration.type.let { typeRef ->
			typeRef::class shouldBe ResolvedTypeRef::class
			(typeRef as ResolvedTypeRef).type::class shouldBe FixeSizeType::class
		}
	}
})