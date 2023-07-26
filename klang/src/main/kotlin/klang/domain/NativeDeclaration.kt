package klang.domain

sealed interface NativeDeclaration {
	fun <T: NativeDeclaration> merge(other: T): Unit = throw UnsupportedOperationException()
}

interface NameableDeclaration: NativeDeclaration {
	val name: String

}