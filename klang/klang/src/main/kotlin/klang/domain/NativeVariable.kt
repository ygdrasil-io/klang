package klang.domain

data class NativeVariable(
	override val name: NotBlankString,
	val type: String,
	override val source: DeclarationOrigin
): NameableDeclaration, NativeDeclaration