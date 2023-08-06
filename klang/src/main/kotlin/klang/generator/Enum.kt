package klang.generator

import klang.domain.KotlinEnumeration

internal fun KotlinEnumeration.generateCode() = """
enum class $name(val value: $type) {
${values.joinToString(separator = ",\n") { "\t${it.first}(${it.second})" }};

	companion object {
		fun of(value: $type): $name? = entries.find { it.value == value }
	}
}
""".trimIndent()

