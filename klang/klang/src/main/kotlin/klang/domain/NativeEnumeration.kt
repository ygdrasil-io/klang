package klang.domain

import klang.DeclarationRepository

val AnonymousEnumeration = "AnonymousEnumeration"

data class NativeEnumeration(
	override val name: String,
	var values: List<Pair<String, Long>> = emptyList(),
	//TODO add support for other types
	var type: TypeRef = typeOf("int").getOrNull() ?: error("Type 'int' not found")
) : NameableDeclaration, ResolvableDeclaration {

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is NativeEnumeration) {
			values += other.values
		} else super.merge(other)
	}

	override fun DeclarationRepository.resolve() {
		type = with(type) { resolveType() }
	}
}