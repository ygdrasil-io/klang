package klang.domain

data class NativeFunction(
	override val name: String,
	val returnType: String,
	val arguments: List<Argument>
): NameableDeclaration, NativeDeclaration {

	data class Argument(
		val name: String?,
		val type: String
	)
}