package klang.domain

import klang.DeclarationRepository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

sealed interface NativeDeclaration {
	fun <T : NativeDeclaration> merge(other: T) {
		logger.debug { "merging $this with $other is not relevant" }
	}

	fun rootType(): NativeDeclaration? = when (this) {
		is PrimitiveType -> this
		is NativeStructure -> this
		is NativeEnumeration -> this.type.let {
			when (it) {
				is ResolvedTypeRef -> it.type.rootType()
				else -> this
			}
		}
		is NativeTypeAlias -> this.typeRef.let {
			when (it) {
				is ResolvedTypeRef -> it.type.rootType()
				else -> this
			}
		}
		is FunctionPointerType -> this
		else -> null
	}
}

interface NameableDeclaration : NativeDeclaration {
	val name: String
}

interface ResolvableDeclaration {
	fun DeclarationRepository.resolve()
}