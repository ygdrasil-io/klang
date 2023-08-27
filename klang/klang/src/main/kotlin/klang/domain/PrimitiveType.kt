package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: String = "void"
}

class FixeSizeType<T>(val size: Int, override val name: String, val defaultValue: T): PrimitiveType()
class PlatformDependantSizeType(val size: IntRange, override val name: String): PrimitiveType()


