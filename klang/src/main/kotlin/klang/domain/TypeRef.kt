package klang.domain

import klang.DeclarationRepository
import klang.findDeclarationByName
import mu.KotlinLogging

@JvmInline
value class TypeNotResolved(val name: String)

fun tokenizeTypeRef(type: String): List<String> {
	val regexPattern = Regex("""\w+\s*<[^>]+>|\*|\w+""")
	return regexPattern.findAll(type).map { it.value.trim() }.toList()
}

sealed class TypeRef(
	val referenceAsString: String
) {

	private val tokens = tokenizeTypeRef(referenceAsString)

	val typeName by lazy {
		referenceAsString.split(" ").first()
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
	override fun toString() = "UnresolvedType($typeName from declaration $referenceAsString)"
}

class ResolvedTypeRef(refName: String, val type: NativeDeclaration) : TypeRef(refName) {
	override fun toString() = "ResolvedType($typeName from declaration $referenceAsString)"
}