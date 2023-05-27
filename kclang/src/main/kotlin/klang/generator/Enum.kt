package klang.generator

private fun Set<String>.generateEnum(name: String) {
	println(
		"""
			enum class $name {
				${joinToString { it }};
				
				companion object {
					fun of(value: String): TranslationUnitKind? = values().find { it.name == value }
				}
			}
		""".trimIndent()
	)
}