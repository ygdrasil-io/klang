package klang.domain

data class NativeEnumeration(
	val name: String,
	val values: List<Pair<String, Int>> = emptyList()
)