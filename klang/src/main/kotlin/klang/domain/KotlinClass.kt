package klang.domain

fun String.isValidClassName(): Boolean {
	val classNameRegex = "[A-Z][a-zA-Z0-9_]*".toRegex()
	return matches(classNameRegex)
}

fun String.isValidMethodName(): Boolean {
	val classNameRegex = "[a-z][a-zA-Z0-9_]*".toRegex()
	return matches(classNameRegex)
}

data class KotlinClass(
	val name: String,
	val members: List<Member>,
	val objectMembers: List<Member>
) {
	init {
		require(name.isValidClassName()) { "name cannot be empty" }
	}

	data class Member(
		val name: String,
		val type: String
	) {
		init {
			require(name.isValidMethodName()) { "name cannot be empty" }
		}
	}
}

/**
 * class representing kotlin annotation
 */
data class KotlinAnnotation(
	val name: String,
	val params: Map<String, String> = emptyMap()
)
