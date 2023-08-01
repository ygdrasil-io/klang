package klang.domain

import klang.DeclarationRepository
import klang.findDeclarationByName
import mu.KotlinLogging

@JvmInline
value class TypeNotResolve(val name: String)

sealed class TypeRef(
	val refName: String
) {

	val typeName by lazy {
		refName.split(" ").first()
	}

	private val logger = KotlinLogging.logger {}

	override fun equals(other: Any?): Boolean {
		return typeName == (other as? TypeRef)?.typeName
	}

	fun DeclarationRepository.resolve(): TypeRef = findDeclarationByName<NameableDeclaration>(typeName)
		?.let { ResolvedTypeRef(typeName, it) }
		?: (UnresolvedTypeRef(typeName)
			.also { logger.warn { "fail to resolve type : $it" } })

}

class UnresolvedTypeRef(refName: String) : TypeRef(refName) {
	override fun toString() = "UnresolvedType($typeName from declaration $refName)"
}

class ResolvedTypeRef(refName: String, val type: NativeDeclaration) : TypeRef(refName) {
	override fun toString() = "ResolvedType($typeName from declaration $refName)"
}