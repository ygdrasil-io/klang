package klang

import klang.domain.*
import mu.KotlinLogging

class InMemoryDeclarationRepository : DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeDeclarations = mutableSetOf<NativeDeclaration>()

	override val declarations: Set<NativeDeclaration>
		get() = nativeDeclarations

	init {
		insertObjectiveCDefaultDeclaration()
		insertCDefaultDeclaration()
	}

	override fun save(declaration: NameableDeclaration) = nativeDeclarations
		.asSequence()
		.filter { it::class == declaration::class }
		.filterIsInstance<NameableDeclaration>()
		.filter { it.name == declaration.name }
		.firstOrNull()
		?.also { logger.debug { "will merge ${it::class.qualifiedName}" } }
		?.merge(declaration)
		?: declaration.let {
			logger.debug { "will insert ${it::class.qualifiedName}}" }
			nativeDeclarations.add(it)
			Unit
		}

	override fun clear() {
		nativeDeclarations.clear()
	}

	override fun update(nativeEnumeration: NativeDeclaration, provider: () -> NativeDeclaration): NativeDeclaration {
		val newValue = provider()
		when (nativeEnumeration) {
			is NativeEnumeration -> logger.debug { "enum updated: $nativeEnumeration to $newValue" }
			is NativeStructure -> logger.debug { "structure updated: $nativeEnumeration to $newValue" }
			is NativeFunction -> logger.debug { "function updated: $nativeEnumeration to $newValue" }
			is NativeTypeAlias -> logger.debug { "type alias updated: $nativeEnumeration to $newValue" }
			else -> throw IllegalArgumentException("Unknown native declaration type: $nativeEnumeration")
		}
		nativeDeclarations.remove(nativeEnumeration)
		nativeDeclarations.add(newValue)
		return newValue
	}


	override fun resolveTypes(filter: (ResolvableDeclaration) -> Boolean) {
		nativeDeclarations
			.asSequence()
			.filterIsInstance<ResolvableDeclaration>()
			.filter(filter)
			.forEach { with(it) { resolve() } }
	}
}