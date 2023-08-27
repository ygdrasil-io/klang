package klang.domain

import klang.DeclarationRepository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

sealed interface NativeDeclaration {
	fun <T : NativeDeclaration> merge(other: T) {
		logger.debug { "merging $this with $other is not relevant" }
	}

	fun rootType(): NativeDeclaration = when (this) {
		is PrimitiveType -> this
		is NativeTypeAlias -> this.type.let {
			when (it) {
				is ResolvedTypeRef -> it.type.rootType()
				else -> this
			}
		}

		else -> error("cannot find root type for $this")
	}
}

interface NameableDeclaration : NativeDeclaration {
	val name: String
}

interface ResolvableDeclaration {
	fun DeclarationRepository.resolve()
}