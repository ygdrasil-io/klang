package klang.domain

data class ObjectiveCClass(
	override val name: String,
	val properties: List<Property>,
	val methods: List<Method>
) : NameableDeclaration, NativeDeclaration {

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

}