package klang

import klang.domain.NativeEnumeration
import klang.domain.NativeFunction
import klang.domain.NativeStructure
import mu.KotlinLogging

object DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeEnumerations = mutableSetOf<NativeEnumeration>()
	private val nativeStructures = mutableSetOf<NativeStructure>()
	private val nativeFunctions = mutableSetOf<NativeFunction>()

	fun save(nativeEnumeration: NativeEnumeration) {
		logger.debug { "enum added: $nativeEnumeration" }
		nativeEnumerations.add(nativeEnumeration)
	}

	fun save(nativeStructure: NativeStructure) {
		logger.debug { "structure added: $nativeStructure" }
		nativeStructures.add(nativeStructure)
	}

	fun save(nativeFunction: NativeFunction) {
		logger.debug { "function added: $nativeFunction" }
		nativeFunctions.add(nativeFunction)
	}

	fun clear() {
		nativeStructures.clear()
		nativeEnumerations.clear()
		nativeFunctions.clear()
	}

	fun findNativeEnumerationByName(name: String) = nativeEnumerations.find { it.name == name }
	fun findNativeStructureByName(name: String) = nativeStructures.find { it.name == name }
	fun findNativeFunctionByName(name: String) = nativeFunctions.find { it.name == name }

}