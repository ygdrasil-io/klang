package klang.domain

data class NativeVariable(override val name: String, val type: String): NameableDeclaration, NativeDeclaration