package generation

import klang.domain.TypeRef

internal fun TypeRef.toKotlinType(): String = when (refName) {
	"void" -> "Unit"
	"BOOL" -> "Boolean"
	else -> this.toString()
}

