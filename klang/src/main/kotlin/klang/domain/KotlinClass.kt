package klang.domain

fun String.isValidClassName(): Boolean {
	val classNameRegex = "[A-Z][a-zA-Z0-9_]*".toRegex()
	return matches(classNameRegex)
}