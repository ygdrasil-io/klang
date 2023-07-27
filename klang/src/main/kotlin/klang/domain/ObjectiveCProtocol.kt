package klang.domain

class ObjectiveCProtocol(
	override val name: String,
	val protocols: Set<String>,
	var properties: List<ObjectiveCClass.Property>,
	var methods: List<ObjectiveCClass.Method>
) : NameableDeclaration