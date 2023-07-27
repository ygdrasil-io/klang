package klang.domain

class ObjectiveCCategory(
	override val name: String,
	val superType: String,
	var methods: List<ObjectiveCClass.Method>
) : NameableDeclaration