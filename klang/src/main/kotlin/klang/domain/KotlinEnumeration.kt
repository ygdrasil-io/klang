package klang.domain

data class KotlinEnumeration(
	val name: String,
	val type: String,
	val values: List<Pair<String, String>>
) {
	init {
	    require(name.isValidClassName()) { "name cannot be empty" }
		require(type.isNotBlank()) { "type cannot be empty" }
		require(values.isNotEmpty()) { "should at least contain one value" }
	}
}
