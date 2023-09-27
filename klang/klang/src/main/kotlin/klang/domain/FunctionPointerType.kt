package klang.domain

import klang.DeclarationRepository

/**
 * Represents a function pointer type
 *
 * @see <a href="https://en.cppreference.com/w/c/language/pointer#Function_pointer">Function pointer</a>
 */
data class FunctionPointerType(
	var returnType: TypeRef,
	var arguments: List<TypeRef>
) : NativeDeclaration, ResolvableDeclaration {

	override fun DeclarationRepository.resolve() {
		returnType = with(returnType) { resolveType() }
		arguments = arguments.map { with(it) { resolveType() } }
	}
}