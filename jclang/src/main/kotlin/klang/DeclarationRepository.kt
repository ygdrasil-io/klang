package klang

import klang.domain.NativeEnumeration
import klang.domain.NativeStructure
import mu.KotlinLogging

object DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeEnumerations = mutableSetOf<NativeEnumeration>()
	private val nativeStructures = mutableSetOf<NativeStructure>()

	fun save(nativeEnumeration: NativeEnumeration) {
		logger.debug { "enum added: $nativeEnumeration" }
		nativeEnumerations.add(nativeEnumeration)
	}

	fun save(nativeStructure: NativeStructure) {
		logger.debug { "structure added: $nativeStructure" }
		nativeStructures.add(nativeStructure)
	}

	fun clear() {
		nativeStructures.clear()
		nativeEnumerations.clear()
	}

	fun findNativeEnumerationByName(name: String) = nativeEnumerations.find { it.name == name }

	fun findNativeStructureByName(name: String) = nativeStructures.find { it.name == name }

}