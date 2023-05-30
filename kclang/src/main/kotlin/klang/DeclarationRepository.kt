package klang

import klang.domain.*
import mu.KotlinLogging

object DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeDeclarations = mutableSetOf<NativeDeclaration>()

	fun save(nativeEnumeration: NativeDeclaration) {
		when (nativeEnumeration) {
			is NativeEnumeration -> logger.debug { "enum added: $nativeEnumeration" }
			is NativeStructure -> logger.debug { "structure added: $nativeEnumeration" }
			is NativeFunction -> logger.debug { "function added: $nativeEnumeration" }
			is NativeTypeAlias -> logger.debug { "type alias added: $nativeEnumeration" }
			else -> throw IllegalArgumentException("Unknown native declaration type: $nativeEnumeration")
		}
		nativeDeclarations.add(nativeEnumeration)
	}

	fun clear() {
		nativeDeclarations.clear()
	}

	fun findEnumerationByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeEnumeration>()
			.find { it.name == name }

	fun findStructureByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeStructure>()
			.find { it.name == name }

	fun findFunctionByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeFunction>()
			.find { it.name == name }

	fun findTypeAliasByName(name: String)= nativeDeclarations
			.filterIsInstance<NativeTypeAlias>()
			.find { it.name == name }

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

}