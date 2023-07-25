package klang.domain

data class NativeTypeAlias(override val name: String, val type: String) :NameableDeclaration, NativeDeclaration
