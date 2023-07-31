package klang.domain

import klang.DeclarationRepository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

sealed interface NativeDeclaration {
	fun <T: NativeDeclaration> merge(other: T) {
		logger.debug { "merging $this with $other is not relevant" }
	}
}

interface NameableDeclaration: NativeDeclaration {
	val name: String
}

interface ResolvableDeclaration {
	fun DeclarationRepository.resolve()
}