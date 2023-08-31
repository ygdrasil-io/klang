package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import klang.domain.*

// TODO add tests
internal fun TypeRef.toType(packageName: String, nullable: Boolean = false) = when {
	isString -> when {
		isArray -> ClassName("kotlin", "Array").parameterizedBy(ClassName("kotlin", "String"))
		else -> ClassName("kotlin", "String")
	}
	isPointer -> when {
		this is ResolvedTypeRef -> when(this.type.rootType()) {
			is PrimitiveType -> jnaPointer
			else -> ClassName(packageName, typeName)
		}
		else -> jnaPointer
	}
	this is ResolvedTypeRef -> when(this.type.rootType()) {
		is VoidType -> ClassName("kotlin", "Unit")
		is PrimitiveType -> toPrimitiveType(packageName)
		else -> ClassName(packageName, typeName)
	}
	else -> ClassName(packageName, typeName)
}.let { if (nullable) it.copy(nullable = true) else it }

// @see https://github.com/java-native-access/jna/blob/master/www/Mappings.md
private fun ResolvedTypeRef.toPrimitiveType(packageName: String): ClassName = this.type.rootType().let { rootType ->
	when {
		// Floating
		rootType is FixeSizeType && rootType.size == 32 && rootType.isFloating -> ClassName("kotlin", "Float")
		rootType is FixeSizeType && rootType.size == 64 && rootType.isFloating -> ClassName("kotlin", "Double")
		// Integer
		rootType is FixeSizeType && rootType.size == 8 -> ClassName("kotlin", "Byte")
		rootType is FixeSizeType && rootType.size == 16 -> ClassName("kotlin", "Short")
		rootType is FixeSizeType && rootType.size == 32 -> ClassName("kotlin", "Int")
		rootType is FixeSizeType && rootType.size == 64 -> ClassName("kotlin", "Long")
		// Specials
		rootType is PlatformDependantSizeType && rootType.size == 16..32 -> ClassName("kotlin", "Char")
		rootType is PlatformDependantSizeType && rootType.size == 32..64 -> ClassName("com.sun.jna", "NativeLong")
		// Default
		else -> ClassName(packageName, typeName)
	}
}


internal val TypeRef.isString: Boolean
	get() = isPointer && typeName == "char"
