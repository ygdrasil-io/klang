package klang

import klang.domain.*
import mu.KotlinLogging

object DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeDeclarations = mutableSetOf<NativeDeclaration>()

	val declarations: Set<NativeDeclaration>
		get() = nativeDeclarations


	init {
		insertObjectiveCDefaultDeclaration()
	}

	fun save(declaration: NameableDeclaration) = nativeDeclarations
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
		}

	fun clear() {
		nativeDeclarations.clear()
	}

	fun findEnumerationByName(name: String) = findDeclarationByName<NativeEnumeration>(name)

	fun findStructureByName(name: String) = findDeclarationByName<NativeStructure>(name)

	fun findFunctionByName(name: String) = findDeclarationByName<NativeFunction>(name)

	fun findTypeAliasByName(name: String) = findDeclarationByName<NativeTypeAlias>(name)

	fun findObjectiveCClassByName(name: String) = findDeclarationByName<ObjectiveCClass>(name)

	fun findObjectiveCProtocolByName(name: String) = findDeclarationByName<ObjectiveCProtocol>(name)

	fun findObjectiveCCategoryByName(name: String) = findDeclarationByName<ObjectiveCCategory>(name)

	fun findDeclarationsByName(declarationName: String) = declarations
		.asSequence()
		.filterIsInstance<NameableDeclaration>()
		.filter { it.name == declarationName }
		.toList()

	inline fun <reified T : NameableDeclaration> findDeclarationByName(declarationName: String) = declarations
		.asSequence()
		.filterIsInstance<T>()
		.firstOrNull { it.name == declarationName }

	fun update(nativeEnumeration: NativeDeclaration, provider: () -> NativeDeclaration): NativeDeclaration {
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


	fun DeclarationRepository.resolve() {
		nativeDeclarations
			.asSequence()
			.filterIsInstance<ResolvableDeclaration>()
			.forEach { with(it) { resolve() } }
	}

}