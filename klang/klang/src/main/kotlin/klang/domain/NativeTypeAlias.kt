package klang.domain

import klang.DeclarationRepository

data class NativeTypeAlias(
	override val name: String,
	var typeRef: TypeRef
) :NameableDeclaration, NativeDeclaration, ResolvableDeclaration {

	override fun DeclarationRepository.resolve() {
		typeRef = with(typeRef) { resolveType() }
		typeRef.resolveIfFunctionPointerType(this)
	}

	private fun TypeRef.resolveIfFunctionPointerType(declarationRepository: DeclarationRepository) {
		if (this is ResolvedTypeRef && type is FunctionPointerType) {
			with(type) { declarationRepository.resolve()}
		}
	}
}


