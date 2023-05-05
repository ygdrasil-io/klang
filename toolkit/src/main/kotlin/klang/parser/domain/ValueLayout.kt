package klang.parser.domain


object ValueLayout {
    
    val BITS_8_LE = MemoryLayout(8)
    val BITS_16_LE = MemoryLayout(16)
    val BITS_32_LE = MemoryLayout(32)
    val BITS_64_LE = MemoryLayout(64)
    val BITS_8_BE = MemoryLayout(8)
    val BITS_16_BE = MemoryLayout(16)
    val BITS_32_BE = MemoryLayout(32)
    val BITS_64_BE = MemoryLayout(64)
    val PAD_8 = MemoryLayout(8)
    val PAD_16 = MemoryLayout(16)
    val PAD_32 = MemoryLayout(32)
    val PAD_64 = MemoryLayout(64)
    val JAVA_BYTE = MemoryLayout(8)
    val JAVA_CHAR = MemoryLayout(16)
    val JAVA_SHORT = MemoryLayout(16, 16)
    val JAVA_INT = MemoryLayout(32, 32)
    val JAVA_LONG = MemoryLayout(64, 64)
    val JAVA_FLOAT = MemoryLayout(32, 32)
    val JAVA_DOUBLE = MemoryLayout(64, 64)
}