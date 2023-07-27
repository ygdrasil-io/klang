package klang.domain

import klang.DeclarationRepository

data class ObjectiveCClass(
	override val name: String,
	val superType: String,
	var protocols: Set<TypeRef>,
	var properties: List<Property>,
	var methods: List<Method>
) : NameableDeclaration {

	data class Property(
		override val name: String,
		val type: String,
		val assign: Boolean = true,
		val readwrite: Boolean = true,
		val nonatomic: Boolean = true,
		val unsafe_unretained: Boolean = true
	) : NameableDeclaration

	data class Method(
		override val name: String,
		val returnType: String,
		val instance: Boolean,
		val arguments: List<Argument> = listOf()
	) : NameableDeclaration {
		data class Argument(
			val name: String,
			val type: String
		)
	}

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is ObjectiveCClass) {
			properties += other.properties
			methods += other.methods
		} else super.merge(other)
	}

	fun DeclarationRepository.resolve() {
		protocols = protocols
			.map { with(it) { resolve() } }
			.toSet()
	}
}