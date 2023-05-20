package klang.domain

data class NativeFunction(
	val name: String,
	val returnType: String,
	val arguments: List<Pair<String, String>>
): NativeDeclaration