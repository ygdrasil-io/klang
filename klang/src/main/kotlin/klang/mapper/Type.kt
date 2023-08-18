package klang.mapper

import com.squareup.kotlinpoet.ClassName
import klang.domain.ResolvedTypeRef
import klang.domain.TypeRef

// @see https://github.com/java-native-access/jna/blob/master/www/Mappings.md

// TODO add tests
internal fun TypeRef.toType(packageName: String): ClassName = when {
	isPointer -> jnaPointer
	isPrimitive -> toPrimitiveType()
	this is ResolvedTypeRef -> ClassName(packageName, typeName)
	else -> ClassName("", typeName)
}

// 8 bits
private val byteType = listOf("char", "Uint8")
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
	"void" -> ClassName("kotlin", "Unit")
	else -> ClassName("", typeName)
}

private val TypeRef.isPrimitive: Boolean
	get() = typeName in listOf(
		"void"
	) + intType + floatType + doubleType + longType + int64Type + byteType + shortType + charType
