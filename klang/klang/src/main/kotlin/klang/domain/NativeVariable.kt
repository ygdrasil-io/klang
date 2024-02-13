package klang.domain

data class NativeVariable(
	override val name: String,
	val type: String,
	override val source: DeclarationOrigin
): NameableDeclaration, NativeDeclaration