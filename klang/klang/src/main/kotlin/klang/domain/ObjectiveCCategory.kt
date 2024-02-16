package klang.domain

import klang.DeclarationRepository

internal val AnonymousCategoryName = "AnonymousCategory"

data class ObjectiveCCategory(
	override val name: NotBlankString,
	var superType: TypeRef,
	val methods: List<ObjectiveCClass.Method>,
	override val source: DeclarationOrigin = DeclarationOrigin.Unknown
) : NameableDeclaration, ResolvableDeclaration {

	override fun DeclarationRepository.resolve() {
		superType = with(superType) { resolveType() }
		// TODO uncomment when ready
		//methods.forEach { with(it) { resolve() } }
	}
}