package klang.domain

import klang.DeclarationRepository

data class NativeFunction(
	override val name: String,
	var returnType: TypeRef,
	val arguments: List<Argument>
): NameableDeclaration, NativeDeclaration, ResolvableDeclaration {

	data class Argument(
		val name: String?,
		var type: TypeRef
	) : ResolvableDeclaration {

		override fun DeclarationRepository.resolve() {
			type = with(type) { resolveType() }
		}
	}

	override fun DeclarationRepository.resolve() {
		returnType = with(returnType) { resolveType() }
		arguments.forEach { with(it) { resolve() } }
	}

}