package klang.domain

import klang.DeclarationRepository

data class NativeFunction(
	override val name: NotBlankString,
	var returnType: TypeRef,
	val arguments: List<Argument>,
	override val source: DeclarationOrigin = DeclarationOrigin.Unknown
): NameableDeclaration, NativeDeclaration, ResolvableDeclaration {

	data class Argument(
		val name: NotBlankString?,
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