package klang.domain

data class NativeStructure(
	override val name: String,
	var fields: List<Pair<String, String>> = listOf()
): NameableDeclaration {

	override fun <T : NativeDeclaration> merge(other: T) {
		if (other is NativeStructure) {
			fields += other.fields
		} else super.merge(other)
	}
}