package klang

import klang.domain.NativeEnumeration

object DeclarationRepository {

	private val nativeEnumerations = mutableSetOf<NativeEnumeration>()

	fun save(nativeEnumeration: NativeEnumeration) {
		nativeEnumerations.add(nativeEnumeration)
	}

	fun clear() {
		nativeEnumerations.clear()
	}

	fun findNativeEnumerationByName(name: String)
		= nativeEnumerations.find { it.name == name }

}