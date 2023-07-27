package klang.domain

import klang.DeclarationRepository

sealed interface NativeDeclaration {
	fun <T: NativeDeclaration> merge(other: T): Unit = throw UnsupportedOperationException()
}

interface NameableDeclaration: NativeDeclaration {
	val name: String
}

interface ResolvableDeclaration {
	fun DeclarationRepository.resolve()
}