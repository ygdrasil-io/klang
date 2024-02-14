package klang.domain

import klang.DeclarationRepository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * This sealed interface represents a native C/C++ or Objective-C declaration.
 */
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

/**
 * This interface represents a nameable declaration.
 */
interface NameableDeclaration : SourceableDeclaration {
	val name: String
}

interface ResolvableDeclaration {
	fun DeclarationRepository.resolve()
}

/**
 * Represents the origin of a native declaration.
 */
sealed interface DeclarationOrigin {

	/**
	 * Represents an unknown origin of a native declaration.
	 */
	object UnknownOrigin : DeclarationOrigin

	/**
	 * Represents a platform-specific header used for native declarations, like libc.
	 */
	object PlatformHeader : DeclarationOrigin

	/**
	 * Represents a header file used for native declarations in a library.
	 *
	 * @property file The path to the header file.
	 */
	class LibraryHeader(val file: String) : DeclarationOrigin
}

interface SourceableDeclaration : NativeDeclaration {
	val source: DeclarationOrigin
}