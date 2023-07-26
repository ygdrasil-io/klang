package klang.domain

sealed interface NativeDeclaration {
	fun merge(other: NativeDeclaration) = UnsupportedOperationException()
}

interface NameableDeclaration {
	val name: String

}