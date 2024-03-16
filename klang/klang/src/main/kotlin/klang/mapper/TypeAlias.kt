package klang.mapper

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.*

internal fun NativeTypeAlias.toSpec(packageName: String) = when {
	typeRef.isCallback -> toCallbackSpec(packageName).unchecked().let { listOf(it) }
	else -> toTypeAliasSpec(packageName)
}

internal fun NativeTypeAlias.toCallbackSpec(
	packageName: String
) = either<String, TypeSpec> {
		val typeRef = typeRef
		ensure(typeRef is ResolvedTypeRef) { "typeRef should be resolved" }
		ensure(typeRef.type is FunctionPointerType) { "Type must be a function pointer" }
		typeRef.type.toCallbackSpec(name.value, packageName)
}

private fun NativeTypeAlias.toTypeAliasSpec(packageName: String) = TypeAliasSpec
	.builder(name.value, typeRef.toType(packageName))
	.build()
	.let { toTypeAliasArraySpec(packageName)?.let { arrayIt -> listOf(it, arrayIt) } ?: listOf(it) }


private fun NativeTypeAlias.toTypeAliasArraySpec(packageName: String) = typeRef.let { typeRef ->
	when(typeRef is ResolvedTypeRef && typeRef.type.rootType() is FixeSizeType) {
		true -> TypeAliasSpec
			.builder("${name.value}${'$'}Array", (typeRef.type.rootType() as FixeSizeType).toArrayType())
			.build()
		false -> null
	}
}

private fun FixeSizeType.toArrayType() = when {
		// Floating
		size == 32 && isFloating -> ClassName("kotlin", "FloatArray")
		size == 64 && isFloating -> ClassName("kotlin", "DoubleArray")
		// Integer
		size == 8 -> ClassName("kotlin", "ByteArray")
		size == 16 -> ClassName("kotlin", "ShortArray")
		size == 32 -> ClassName("kotlin", "IntArray")
		size == 64 -> ClassName("kotlin", "LongArray")
		// Default
		else -> error("unreachable statement")
	}

