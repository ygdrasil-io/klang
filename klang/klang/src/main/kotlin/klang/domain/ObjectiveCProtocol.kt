package klang.domain

data class ObjectiveCProtocol(
	override val name: String,
	val protocols: Set<String>,
	var properties: List<ObjectiveCClass.Property>,
	var methods: List<ObjectiveCClass.Method>,
	override val source: DeclarationOrigin = DeclarationOrigin.UnknownOrigin
) : NameableDeclaration