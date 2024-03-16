package klang.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import klang.DeclarationRepository
import klang.findDeclarationByName
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun <A, B> Either<A, B>.unchecked(message: String = "unchecked Either lead to error") = getOrNull()
	?: error(message)

fun typeOf(reference: String): Either<String, TypeRef> = either {
	var isArray = false
	var arraySize: Int? = null
	val tokens = tokenizeTypeRef(reference).toMutableList()
	ensure(tokens.isNotEmpty()) { "fail to tokenize type $reference" }

	val isConstant = tokens.takeIf { it.first() == "const" }?.let {
		it.removeFirst()
		true
	} ?: false

	val isStructure = tokens.takeIf { it.first() == "struct" }?.let {
		it.removeFirst()
		true
	} ?: false

	val isEnumeration = tokens.takeIf { it.first() == "enum" }?.let {
		it.removeFirst()
		true
	} ?: false

	val isVolatile = tokens.takeIf { it.first() == "volatile" }?.let {
		it.removeFirst()
		true
	} ?: false

	val isUnion = tokens.takeIf { it.first() == "union" }?.let {
		it.removeFirst()
		true
	} ?: false

	ensure(tokens.isNotEmpty()) { "type name not found $reference" }
	var typeName = tokens.takeIf { it.first() == "unsigned" }?.let {
		it.removeFirst()
		ensure(tokens.isNotEmpty()) { "type name not found $reference" }
		"unsigned ${tokens.removeFirst()}"
	} ?: tokens.removeFirst()

	val isPointer = tokens.takeIf { it.firstOrNull() == "*" }?.let {
		it.removeFirst()
		true
	} ?: false

	val isNullable = tokens.takeIf { it.firstOrNull() == "_Nullable" }?.let {
		it.removeFirst()
		true
	} ?: tokens.takeIf { it.firstOrNull() == "_Nonnull" }?.let {
		it.removeFirst()
		false
	}

	//TODO find a better way to handle this
	val isCallback = reference.contains("(") && isPointer

	if (isCallback) {
		typeName = reference
	} else {
		if (reference.contains("[") && reference.contains("]")) {
			isArray = true
			arraySize = reference.substringAfter("[").substringBefore("]").toIntOrNull()
		}
	}

	if (isEnumeration) {
		ensure(!isStructure) {
			"type cannot be an enumeration and a structure $reference"
		}
	}
	UnresolvedTypeRef(
		reference,
		NotBlankString(typeName),
		isConstant,
		isPointer,
		isStructure,
		isEnumeration,
		isNullable,
		isVolatile,
		isUnion,
		isCallback,
		isArray,
		arraySize
	)
}

sealed interface TypeRef {

	val referenceAsString: String
	val typeName: NotBlankString
	val isConstant: Boolean
	val isPointer: Boolean
	val isStructure: Boolean
	val isEnumeration: Boolean
	val isNullable: Boolean?
	val isVolatile: Boolean
	val isUnion: Boolean
	var isArray: Boolean
	var arraySize: Int?
	val isCallback: Boolean

	fun DeclarationRepository.resolveType(): TypeRef = when {
		this@TypeRef is ResolvedTypeRef -> this@TypeRef
		isCallback -> ResolvedTypeRef(this@TypeRef, typeName.toFunctionPointerType())
		isPointer && typeName == NotBlankString("char") -> ResolvedTypeRef(this@TypeRef, StringType)
		else -> findDeclarationByName<NameableDeclaration>(typeName)
				?.let { ResolvedTypeRef(this@TypeRef, it) }
				?: (this@TypeRef.also { logger.warn { "fail to resolve type : $it" } })
	}

}

internal fun NotBlankString.toFunctionPointerType(): FunctionPointerType {
	val returnType = value.substringBefore("(").let { typeOf(it).unchecked() }

	val arguments = value.substringAfter("(*)")
		.replace("(", "")
		.replace(")", "")
		.split(",")
		.map { typeOf(it.trim()).unchecked() }

	return FunctionPointerType(
		returnType = returnType,
		arguments = arguments
	)
}

class UnresolvedTypeRef internal constructor(
	override val referenceAsString: String,
	override val typeName: NotBlankString,
	override val isConstant: Boolean = false,
	override val isPointer: Boolean = false,
	override val isStructure: Boolean = false,
	override val isEnumeration: Boolean = false,
	override val isNullable: Boolean? = null,
	override val isVolatile: Boolean = false,
	override val isUnion: Boolean = false,
	override val isCallback: Boolean = false,
	override var isArray: Boolean = false,
	override var arraySize: Int? = null,
) : TypeRef {

	override fun toString() = "UnresolvedType($typeName from declaration $referenceAsString)"

	override fun equals(other: Any?): Boolean {
		return typeName == (other as? TypeRef)?.typeName
	}

	override fun hashCode(): Int {
		return typeName.hashCode()
	}

}

class ResolvedTypeRef internal constructor(private val typeRef: TypeRef, val type: NativeDeclaration) :
	TypeRef by typeRef {
	override fun toString() = "ResolvedType($typeName from declaration $referenceAsString)"

	override fun equals(other: Any?) = typeRef == other
	override fun hashCode(): Int = typeRef.hashCode()
}

private fun tokenizeTypeRef(type: String): List<String> {
	val regexPattern = Regex("""\w+\s*<[^>]+>|\*|\w+""")
	return regexPattern.findAll(type).map { it.value.trim() }.toList()
}