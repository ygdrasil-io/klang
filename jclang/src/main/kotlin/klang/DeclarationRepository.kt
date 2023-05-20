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

}