package klang

import klang.domain.*
import mu.KotlinLogging

object DeclarationRepository {

	private val logger = KotlinLogging.logger {}
	private val nativeDeclarations = mutableSetOf<NativeDeclaration>()

	fun save(nativeEnumeration: NativeDeclaration) {
		when (nativeEnumeration) {
			is NativeEnumeration -> {
				if (nativeEnumeration.values.isEmpty()) {
					logger.warn { "try to add enumeration with no value" }
					return
				}

				if (findEnumerationByName(nativeEnumeration.name) != null) {
					logger.warn { "try to add enumeration multiple time" }
					return
				}

				logger.debug { "enum added: $nativeEnumeration" }
			}

			is NativeStructure -> logger.debug { "structure added: $nativeEnumeration" }
			is NativeFunction -> logger.debug { "function added: $nativeEnumeration" }
			is NativeTypeAlias -> logger.debug { "type alias added: $nativeEnumeration" }
			is ObjectiveCClass -> logger.debug { "objective-c class added: $nativeEnumeration" }
			else -> throw IllegalArgumentException("Unknown native declaration type: $nativeEnumeration")
		}
		nativeDeclarations.add(nativeEnumeration)
	}

	fun clear() {
		nativeDeclarations.clear()
	}

	fun findEnumerationByName(name: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<NativeEnumeration>()
		.find { it.name == name }

	fun findStructureByName(name: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<NativeStructure>()
		.find { it.name == name }

	fun findFunctionByName(name: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<NativeFunction>()
		.find { it.name == name }

	fun findTypeAliasByName(name: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<NativeTypeAlias>()
		.find { it.name == name }

	fun findObjectiveCClassByName(name: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<ObjectiveCClass>()
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

	fun findDeclarationByName(declarationName: String) = nativeDeclarations
		.asSequence()
		.filterIsInstance<NameableDeclaration>()
		.first { it.name == declarationName }


}