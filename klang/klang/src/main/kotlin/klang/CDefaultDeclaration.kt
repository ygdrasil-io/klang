package klang

import klang.domain.FixeSizeType
import klang.domain.PlatformDependantSizeType
import klang.domain.VoidType

fun DeclarationRepository.insertCDefaultDeclaration() {
	save(VoidType)
	save(PlatformDependantSizeType(32..64, "size_t"))
	byteType.forEach { save(FixeSizeType(8, it)) }
	shortType.forEach { save(FixeSizeType(16, it)) }
	intType.forEach { save(FixeSizeType(32, it)) }
	int64Type.forEach { save(FixeSizeType(64, it)) }
	floatType.forEach { save(FixeSizeType(32, it, true)) }
	doubleType.forEach { save(FixeSizeType(64, it, true)) }
	longType.forEach { save(PlatformDependantSizeType(32..64, it)) }
	charType.forEach { save(PlatformDependantSizeType(16..32, it)) }
}

// 8 bits
private val byteType = listOf("char", "unsigned char", "uint16_t", "int16_t")

// 16 bits
private val shortType = listOf("short", "unsigned short", "uint8_t", "int8_t")

// 32 bits
private val intType = listOf("int", "unsigned int", "uint32_t", "int32_t")

// 32 to 64 bits
private val longType = listOf("long", "unsigned long")

// 32 to 64 bits
private val charType = listOf("wchar_t")

// 64 bits
private val int64Type = listOf("uint64_t", "int64_t")

// 32 bits
private val floatType = listOf("float")

// 64 bits
private val doubleType = listOf("double")