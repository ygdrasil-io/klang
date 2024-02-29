package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: NotBlankString = NotBlankString("void")
	override val source: DeclarationOrigin = DeclarationOrigin.Platform
}

class FixeSizeType(val size: Int, override val name: NotBlankString, val isFloating: Boolean = false, override val source: DeclarationOrigin = DeclarationOrigin.Platform): PrimitiveType()
class PlatformDependantSizeType(val size: IntRange, override val name: NotBlankString, override val source: DeclarationOrigin = DeclarationOrigin.Platform): PrimitiveType()

data object StringType: PrimitiveType() {
	override val name: NotBlankString = NotBlankString("char *")
	override val source: DeclarationOrigin = DeclarationOrigin.Platform
}


