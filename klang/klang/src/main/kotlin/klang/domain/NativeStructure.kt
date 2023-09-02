package klang.domain

import klang.DeclarationRepository

data class NativeStructure(
	override val name: String,
	var fields: List<Pair<String, TypeRef>> = listOf(),
	var isUnion: Boolean = false,
): NameableDeclaration, ResolvableDeclaration {

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is NativeStructure) {
			fields += other.fields
		} else super.merge(other)
	}

	override fun DeclarationRepository.resolve() {
		fields = fields.map { (name, type) ->
			name to with(type) { resolveType() }
		}
	}
}