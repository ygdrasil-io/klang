package klang.parser.domain

data class MemoryLayout(
    val paddingLayout: Long,
    val withBitAlignment: Long? = null,
    val name: String? = null
) {
    companion object {
        fun structLayout(fields: Array<MemoryLayout>): MemoryLayout {
            return MemoryLayout(fields.sumOf { it.paddingLayout }, fields.sumOf { it.withBitAlignment ?: 0 })
        }

        fun unionLayout(fields: Array<MemoryLayout>): MemoryLayout {
            TODO("Not yet implemented")
        }
    }
}