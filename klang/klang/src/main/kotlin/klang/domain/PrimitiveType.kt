package klang.domain

sealed class PrimitiveType: NameableDeclaration

data object VoidType: PrimitiveType() {
	override val name: NotBlankString = NotBlankString("void")
	override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader
}

class FixeSizeType(val size: Int, override val name: NotBlankString, val isFloating: Boolean = false, override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader): PrimitiveType()
class PlatformDependantSizeType(val size: IntRange, override val name: NotBlankString, override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader): PrimitiveType()

data object StringType: PrimitiveType() {
	override val name: NotBlankString = NotBlankString("char *")
	override val source: DeclarationOrigin = DeclarationOrigin.PlatformHeader
}


