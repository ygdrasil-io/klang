package klang.domain

data class NativeFunction(
	val name: String,
	val returnType: String,
	val arguments: List<Argument>
): NativeDeclaration {

	data class Argument(
		val name: String?,
		val type: String
	)
}