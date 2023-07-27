package klang.domain

import klang.DeclarationRepository

data class ObjectiveCClass(
	override val name: String,
	var superType: TypeRef,
	var protocols: Set<TypeRef>,
	var properties: List<Property>,
	var methods: List<Method>
) : NameableDeclaration, ResolvableDeclaration {

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
			properties += other.properties
			methods += other.methods
		} else super.merge(other)
	}

	override fun DeclarationRepository.resolve() {
		protocols = protocols
			.map { with(it) { resolve() } }
			.toSet()
		superType = with(superType) { resolve() }
	}
}