package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: String = "void"
	override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader
}

class FixeSizeType(val size: Int, override val name: String, val isFloating: Boolean = false, override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader): PrimitiveType()
class PlatformDependantSizeType(val size: IntRange, override val name: String, override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader): PrimitiveType()

data object StringType: PrimitiveType() {
	override val name: String = "char *"
	override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader
}


