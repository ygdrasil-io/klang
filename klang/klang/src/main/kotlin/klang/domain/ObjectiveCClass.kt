package klang.domain

import klang.DeclarationRepository

data class ObjectiveCClass(
	override val name: NotBlankString,
	var superType: TypeRef?,
	var protocols: Set<TypeRef>,
	var properties: List<Property>,
	var methods: List<Method>,
	var categories: Set<ObjectiveCCategory> = setOf(),
	override val source: DeclarationOrigin = DeclarationOrigin.UnknownOrigin
) : NameableDeclaration, ResolvableDeclaration {

	data class Property(
		override val name: NotBlankString,
		val type: String,
		val assign: Boolean? = null,
		val readwrite: Boolean? = null,
		val nonatomic: Boolean? = null,
		val unsafe_unretained: Boolean? = null,
		override val source: DeclarationOrigin= DeclarationOrigin.UnknownOrigin
	) : NameableDeclaration

	data class Method(
		override val name: NotBlankString,
		var returnType: TypeRef,
		val instance: Boolean,
		val arguments: List<Argument> = listOf(),
		override val source: DeclarationOrigin = DeclarationOrigin.UnknownOrigin
	) : NameableDeclaration, ResolvableDeclaration {
		data class Argument(
			val name: String,
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

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is ObjectiveCClass) {
			protocols += other.protocols
			properties += other.properties
			methods += other.methods
		} else super.merge(other)
	}

	override fun DeclarationRepository.resolve() {
		protocols = protocols
			.map { with(it) { resolveType() } }
			.toSet()
		superType = superType?.let { with(it) { resolveType() } }
		categories = declarations
			.asSequence()
			.filterIsInstance<ObjectiveCCategory>()
			.filter { it.superType.referenceAsString == name.value }
			.toSet()

		methods.forEach { with(it) { resolve() } }
	}

}