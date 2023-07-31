package klang.domain

import klang.DeclarationRepository

data class ObjectiveCClass(
	override val name: String,
	var superType: TypeRef?,
	var protocols: Set<TypeRef>,
	var properties: List<Property>,
	var methods: List<Method>,
	var categories: Set<ObjectiveCCategory> = setOf()
) : NameableDeclaration, ResolvableDeclaration {

	data class Property(
		override val name: String,
		val type: String,
		val assign: Boolean? = null,
		val readwrite: Boolean? = null,
		val nonatomic: Boolean? = null,
		val unsafe_unretained: Boolean? = null
	) : NameableDeclaration

	data class Method(
		override val name: String,
		val returnType: TypeRef,
		val instance: Boolean,
		val arguments: List<Argument> = listOf()
	) : NameableDeclaration, ResolvableDeclaration {
		data class Argument(
			val name: String,
			val type: String
		) : ResolvableDeclaration {

			override fun DeclarationRepository.resolve() {
				TODO()
			}
		}

		override fun DeclarationRepository.resolve() {
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
			.map { with(it) { resolve() } }
			.toSet()
		superType = superType?.let { with(it) { resolve() } }
		categories = declarations
			.asSequence()
			.filterIsInstance<ObjectiveCCategory>()
			.filter { it.superType.refName == name }
			.toSet()
	}

}