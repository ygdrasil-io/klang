package klang.mapper

import klang.domain.KotlinEnumeration
import klang.domain.NativeEnumeration

fun NativeEnumeration.toKotlinEnumeration() = KotlinEnumeration(
	name = name,
	type = "Long",
	values = values.map { (name, value) -> name to value.toString() }
)

