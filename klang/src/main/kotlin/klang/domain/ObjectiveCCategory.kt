package klang.domain

import klang.DeclarationRepository

data class ObjectiveCCategory(
	override val name: String,
	var superType: TypeRef,
	val methods: List<ObjectiveCClass.Method>
) : NameableDeclaration, ResolvableDeclaration {

	override fun DeclarationRepository.resolve() {
		superType = with(superType) { resolve() }
		// TODO uncomment when ready
		//methods.forEach { with(it) { resolve() } }
	}
}