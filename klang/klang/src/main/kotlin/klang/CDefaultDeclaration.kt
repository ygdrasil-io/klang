package klang

import klang.domain.FixeSizeType
import klang.domain.PlatformDependantSizeType
import klang.domain.VoidType

fun DeclarationRepository.insertCDefaultDeclaration() {
	save(VoidType)
	byteType.forEach { save(FixeSizeType<Byte>(8, it, 0)) }
	shortType.forEach { save(FixeSizeType<Short>(16, it, 0)) }
	intType.forEach { save(FixeSizeType<Int>(32, it, 0)) }
	int64Type.forEach { save(FixeSizeType<Long>(64, it, 0)) }
	floatType.forEach { save(FixeSizeType<Float>(32, it, 0f)) }
	doubleType.forEach { save(FixeSizeType<Double>(64, it, 0.0)) }
	longType.forEach { save(PlatformDependantSizeType(16..32, it)) }
	charType.forEach { save(PlatformDependantSizeType(32..64, it)) }
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