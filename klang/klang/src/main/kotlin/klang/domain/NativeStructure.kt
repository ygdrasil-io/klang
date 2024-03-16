package klang.domain

import klang.DeclarationRepository

/**
 * Interface representing a field in the native structure,
 * which can be either a TypeRef or a NativeStructure
 */
sealed interface StructureField {
	val name: String
}

data class TypeRefField(override val name: String, val type: TypeRef) : StructureField
data class SubStructureField(override val name: String, val structure: NativeStructure) : StructureField

/**
 * Represents a native structure declaration.
 *
 * @property name The name of the structure
 * @property fields The list of fields in the structure, each represented by a StructureField
 * @property isUnion Indicates whether the structure is a union
 */
data class NativeStructure(
    override val name: NotBlankString,
    var fields: List<StructureField> = listOf(),
    var isUnion: Boolean = false,
    override val source: DeclarationOrigin = DeclarationOrigin.Unknown,
) : NameableDeclaration, ResolvableDeclaration {
	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is NativeStructure) {
			fields = other.fields
		} else super.merge(other)
	}

	override fun DeclarationRepository.resolve() {
		fields = fields.map { field ->
			when (field) {
				is TypeRefField -> TypeRefField(field.name, with(field.type) { resolveType() })
					.also { resolve(it.type) }

				is SubStructureField -> SubStructureField(field.name, field.structure)
					.also { with(it.structure) { resolve() } }
			}
		}
	}
}

private fun DeclarationRepository.resolve(typeRef: TypeRef) {
	(typeRef as? ResolvedTypeRef)
		?.let(ResolvedTypeRef::type)
		?.let { it as? FunctionPointerType }
		?.let { with(it) { resolve() } }
}

