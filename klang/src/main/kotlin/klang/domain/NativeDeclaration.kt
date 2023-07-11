package klang.domain

sealed interface NativeDeclaration

interface NameableDeclaration {
	val name: String
}