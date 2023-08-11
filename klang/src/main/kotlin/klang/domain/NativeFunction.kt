package klang.domain

data class NativeFunction(
	override val name: String,
	val returnType: TypeRef,
	val arguments: List<Argument>
): NameableDeclaration, NativeDeclaration {

	data class Argument(
		val name: String?,
		val type: TypeRef
	)

}