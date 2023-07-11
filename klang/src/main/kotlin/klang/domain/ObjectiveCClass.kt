package klang.domain

data class ObjectiveCClass(
	val name: String,
	val properties: List<Property>,
	val methods: List<String>
) : NativeDeclaration {

	data class Property(
		override val name: String,
		val type: String,
		val assign: Boolean = true,
		val readwrite: Boolean = true,
		val nonatomic: Boolean = true,
		val unsafe_unretained: Boolean = true
	) : NameableDeclaration

	data class Method(
		val name: String,
		val returnType: String,
		val arguments: List<NativeFunction.Argument>
	) {
		data class Argument(
			val name: String,
			val type: String
		)
	}

}