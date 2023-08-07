package klang.generator

import com.squareup.kotlinpoet.*
import klang.domain.KotlinEnumeration
import klang.domain.NativeEnumeration

internal fun KotlinEnumeration.generateCode() = """
enum class $name(val value: $type) {
${values.joinToString(separator = ",\n") { "\t${it.first}(${it.second})" }};

	companion object {
		fun of(value: $type): $name? = entries.find { it.value == value }
	}
}
""".trimIndent()




