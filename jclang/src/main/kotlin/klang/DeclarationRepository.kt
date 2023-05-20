package klang

import klang.domain.NativeDeclaration
import klang.domain.NativeEnumeration
import klang.domain.NativeFunction
import klang.domain.NativeStructure
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

	fun findNativeEnumerationByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeEnumeration>()
			.find { it.name == name }

	fun findNativeStructureByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeStructure>()
			.find { it.name == name }

	fun findNativeFunctionByName(name: String) = nativeDeclarations
			.filterIsInstance<NativeFunction>()
			.find { it.name == name }

}