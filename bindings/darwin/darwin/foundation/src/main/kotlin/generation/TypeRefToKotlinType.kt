package generation

import klang.domain.ResolvedTypeRef
import klang.domain.TypeRef
import klang.domain.UnresolvedTypeRef

internal fun TypeRef.toKotlinType(): String = when (this) {
	is UnresolvedTypeRef -> toKotlinType()
	is ResolvedTypeRef -> toKotlinType()
}

private fun ResolvedTypeRef.toKotlinType(): String = typeName.toString()

private fun UnresolvedTypeRef.toKotlinType(): String = when (referenceAsString) {
	"void" -> "Unit"
	"BOOL" -> "Boolean"
	else -> this.toString()
}
