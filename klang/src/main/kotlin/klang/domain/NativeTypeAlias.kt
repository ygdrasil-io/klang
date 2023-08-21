package klang.domain

import klang.DeclarationRepository

data class NativeTypeAlias(
	override val name: String,
	var type: TypeRef
) :NameableDeclaration, NativeDeclaration, ResolvableDeclaration {

	override fun DeclarationRepository.resolve() {
		type = with(type) { resolveType() }
	}
}
