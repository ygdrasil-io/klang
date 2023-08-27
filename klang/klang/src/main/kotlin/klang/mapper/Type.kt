package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import klang.domain.ResolvedTypeRef
import klang.domain.TypeRef

// @see https://github.com/java-native-access/jna/blob/master/www/Mappings.md

// TODO add tests
internal fun TypeRef.toType(packageName: String, nullable: Boolean = false) = when {
	isString -> when {
		isArray -> ClassName("kotlin", "Array").parameterizedBy(ClassName("kotlin", "String"))
		else -> ClassName("kotlin", "String")
	}
	isPointer -> when {
		this is ResolvedTypeRef -> ClassName(packageName, typeName)
		else -> jnaPointer
	}
	isSpecialType() -> ClassName("kotlin", "Unit")
	isPrimitive -> toPrimitiveType()
	this is ResolvedTypeRef -> ClassName(packageName, typeName)
	else -> ClassName("", typeName)
}.let { if (nullable) it.copy(nullable = true) else it }

// 8 bits
private val byteType = listOf("char", "Uint8", "unsigned char")

// 16 bits
private val shortType = listOf("short", "__uint16_t", "unsigned short")

// 32 bits
private val intType = listOf("int", "Uint32", "size_t", "unsigned int", "__uint32_t")

// 32 to 64 bits
private val longType = listOf("long", "unsigned long")

// 32 to 64 bits
private val charType = listOf("wchar_t")

// 64 bits
private val int64Type = listOf("Uint64", "uint64_t", "__uint64_t")

// 32 bits
private val floatType = listOf("float")

// 64 bits
private val doubleType = listOf("double")

private fun TypeRef.toPrimitiveType(): ClassName = when (typeName) {
	in intType -> ClassName("kotlin", "Int")
	in int64Type -> ClassName("kotlin", "Long")
	in floatType -> ClassName("kotlin", "Float")
	in doubleType -> ClassName("kotlin", "Double")
	in longType -> ClassName("com.sun.jna", "NativeLong")
	in byteType -> ClassName("kotlin", "Byte")
	in shortType -> ClassName("kotlin", "Short")
	in charType -> ClassName("kotlin", "Char")
	else -> ClassName("", typeName)
}

private fun TypeRef.isSpecialType() = typeName == "void"

internal val TypeRef.isPrimitive: Boolean
	get() = typeName in intType + floatType + doubleType + longType + int64Type + byteType + shortType + charType

internal val TypeRef.isFloat: Boolean
	get() = typeName in floatType

internal val TypeRef.isDouble: Boolean
	get() = typeName in doubleType

internal val TypeRef.isInt64: Boolean
	get() = typeName in int64Type

internal val TypeRef.isLong: Boolean
	get() = typeName in longType

internal val TypeRef.isString: Boolean
	get() = isPointer && typeName == "char"

internal val TypeRef.defaultValue: String
	get() = when {
		isString -> "\"\""
		isPointer -> "null"
		isLong -> "com.sun.jna.NativeLong(0)"
		isPrimitive -> when {
			isFloat -> "0.0f"
			isDouble -> "0.0"
			isInt64 -> "0L"
			else -> "0"
		}

		else -> "null"
	}