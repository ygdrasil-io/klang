package klang.domain

data class NativeEnumeration(
	override val name: String,
	val values: List<Pair<String, Long>> = emptyList()
) : NameableDeclaration, NativeDeclaration