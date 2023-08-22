package klang.domain

val AnonymousEnumeration = "AnonymousEnumeration"

data class NativeEnumeration(
	override val name: String,
	var values: List<Pair<String, Long>> = emptyList()
) : NameableDeclaration {

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is NativeEnumeration) {
			values += other.values
		} else super.merge(other)
	}
}