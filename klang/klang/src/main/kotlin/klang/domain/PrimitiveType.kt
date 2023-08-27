package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: String = "void"
}

sealed class FixeSizeType(size: Int): PrimitiveType()
