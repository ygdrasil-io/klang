package klang.domain

import klang.DeclarationRepository
import klang.findDeclarationByName
import mu.KotlinLogging

sealed class TypeRef(
	val refName: String
) {

	private val logger = KotlinLogging.logger {}

	override fun equals(other: Any?): Boolean {
		return refName == (other as? TypeRef)?.refName
	}

	fun DeclarationRepository.resolve(): TypeRef = findDeclarationByName<NameableDeclaration>(refName)
		?.let { ResolvedTypeRef(refName, it) }
		?: (UnresolvedTypeRef(refName)
			.also { logger.warn { "fail to resolve type ref: $it" } })

}

class UnresolvedTypeRef(refName: String) : TypeRef(refName) {
	override fun toString() = "UnresolvedType($refName)"
}

class ResolvedTypeRef(refName: String, val type: NativeDeclaration) : TypeRef(refName) {
	override fun toString() = "ResolvedType($refName)"
}