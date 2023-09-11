package klang.mapper

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.squareup.kotlinpoet.*
import klang.domain.FunctionPointerType
import klang.domain.NativeTypeAlias
import klang.domain.ResolvedTypeRef
import klang.domain.unchecked

internal fun NativeTypeAlias.toSpec(packageName: String) = when {
	typeRef.isCallback -> toCallbackSpec(packageName).unchecked()
	else -> toTypeAliasSpec(packageName)
}

internal fun NativeTypeAlias.toCallbackSpec(
	packageName: String
) = either<String, TypeSpec> {
		val typeRef = typeRef
		ensure(typeRef is ResolvedTypeRef) { "typeRef should be resolved" }
		ensure(typeRef.type is FunctionPointerType) { "Type must be a function pointer" }
		typeRef.type.toCallbackSpec(name, packageName)

}

private fun NativeTypeAlias.toTypeAliasSpec(packageName: String) = TypeAliasSpec
	.builder(name, typeRef.toType(packageName))
	.build()



