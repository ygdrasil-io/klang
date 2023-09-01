package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: String = "void"
}

class FixeSizeType(val size: Int, override val name: String, val isFloating: Boolean = false): PrimitiveType()
class PlatformDependantSizeType(val size: IntRange, override val name: String): PrimitiveType()

data object StringType: PrimitiveType() {
	override val name: String = "char *"
}


