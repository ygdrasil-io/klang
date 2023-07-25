package klang.domain

data class NativeStructure(
	override val name: String,
	val fields: List<Pair<String, String>> = listOf()
): NameableDeclaration, NativeDeclaration