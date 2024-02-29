package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import klang.domain.*

// TODO add tests
internal fun TypeRef.toType(packageName: String, nullable: Boolean = false, fromStructure: Boolean = false) = when {
	isPointer -> when {
		this is ResolvedTypeRef -> when (this.type.rootType()) {
			is StringType -> when {
				isArray -> ClassName("kotlin", "Array").parameterizedBy(ClassName("kotlin", "String"))
				else -> ClassName("kotlin", "String")
			}

			is NativeStructure -> when (fromStructure) {
				true -> jnaNullablePointer
				else -> ClassName(packageName, typeName.value)
			}
			is FunctionPointerType -> jnaCallback
			is PrimitiveType -> jnaPointer
			else -> ClassName(packageName, typeName.value)
		}

		else -> jnaPointer
	}
	this is ResolvedTypeRef && type is NativeTypeAlias -> ClassName(packageName, type.name.value)
	this is ResolvedTypeRef -> when (this.type.rootType()) {
		is FunctionPointerType -> jnaCallback
		is VoidType -> ClassName("kotlin", "Unit")
		is PrimitiveType -> toPrimitiveType(packageName)
		else -> ClassName(packageName, typeName.value)
	}

	else -> ClassName(packageName, typeName.value)
}.let { if (nullable) it.copy(nullable = true) else it }

// @see https://github.com/java-native-access/jna/blob/master/www/Mappings.md
private fun ResolvedTypeRef.toPrimitiveType(packageName: String): ClassName = this.type.rootType().let { rootType ->
	when {
		isArray -> when (rootType) {
			is FixeSizeType -> when {
				// Floating
				rootType.size == 32 && rootType.isFloating -> ClassName("kotlin", "FloatArray")
				rootType.size == 64 && rootType.isFloating -> ClassName("kotlin", "DoubleArray")
				// Integer
				rootType.size == 8 -> ClassName("kotlin", "ByteArray")
				rootType.size == 16 -> ClassName("kotlin", "ShortArray")
				rootType.size == 32 -> ClassName("kotlin", "IntArray")
				rootType.size == 64 -> ClassName("kotlin", "LongArray")
				// Default
				else -> null
			}

			// Specials
			is PlatformDependantSizeType -> when (rootType.size) {
				16..32 -> ClassName("kotlin", "CharArray")
				32..64 -> ClassName("com.sun.jna", "NativeLong")
				// Default
				else -> null
			}
			// Default
			else -> null
		}

		else -> when (rootType) {
			is FixeSizeType -> when {
				// Floating
				rootType.size == 32 && rootType.isFloating -> ClassName("kotlin", "Float")
				rootType.size == 64 && rootType.isFloating -> ClassName("kotlin", "Double")
				// Integer
				rootType.size == 8 -> ClassName("kotlin", "Byte")
				rootType.size == 16 -> ClassName("kotlin", "Short")
				rootType.size == 32 -> ClassName("kotlin", "Int")
				rootType.size == 64 -> ClassName("kotlin", "Long")
				// Default
				else -> null
			}

			// Specials
			is PlatformDependantSizeType -> when (rootType.size) {
				16..32 -> ClassName("kotlin", "Char")
				32..64 -> ClassName("com.sun.jna", "NativeLong")
				// Default
				else -> null
			}
			// Default
			else -> null
		}
	} ?: ClassName(packageName, typeName.value)

}