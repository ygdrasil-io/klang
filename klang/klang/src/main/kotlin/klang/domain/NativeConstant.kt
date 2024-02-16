package klang.domain

data class NativeConstant<T>(
	override val name: NotBlankString,
	val value: T,
	override val source: DeclarationOrigin = DeclarationOrigin.Unknown
) : NameableDeclaration