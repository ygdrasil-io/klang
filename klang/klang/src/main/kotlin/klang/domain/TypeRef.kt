package klang.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import klang.DeclarationRepository
import klang.findDeclarationByName
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun typeOf(reference: String): Either<String, TypeRef> = either{
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
	val typeName = tokens.takeIf { it.first() == "unsigned" }?.let {
		it.removeFirst()
		ensure(tokens.isNotEmpty()) { "type name not found $reference" }
		"unsigned ${tokens.removeFirst()}"
	} ?:tokens.removeFirst()

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

	if (isEnumeration) {
		ensure(!isStructure) {
			"type cannot be an enumeration and a structure $reference"
		}
	}
	UnresolvedTypeRef(
		reference,
		typeName,
		isConstant,
		isPointer,
		isStructure,
		isEnumeration,
		isNullable,
		isVolatile,
		isUnion
	)
}

sealed interface TypeRef{

	val referenceAsString: String
	val typeName: String
	val isConstant: Boolean
	val isPointer: Boolean
	val isStructure: Boolean
	val isEnumeration: Boolean
	val isNullable: Boolean?
	val isVolatile: Boolean
	val isUnion: Boolean
	var isArray: Boolean
	var isPrimitive: Boolean

	fun DeclarationRepository.resolveType(): TypeRef = findDeclarationByName<NameableDeclaration>(typeName)
		?.let { ResolvedTypeRef(this@TypeRef, it) }
		?: (this@TypeRef.also { logger.warn { "fail to resolve type : $it" } })

}

class UnresolvedTypeRef internal constructor(
	override val referenceAsString: String,
	override val typeName: String,
	override val isConstant: Boolean,
	override val isPointer: Boolean,
	override val isStructure: Boolean,
	override val isEnumeration: Boolean,
	override val isNullable: Boolean?,
	override val isVolatile: Boolean,
	override val isUnion: Boolean
) : TypeRef {

	override var isArray: Boolean = false
	override var isPrimitive: Boolean = typeName in intType + floatType + doubleType + longType + int64Type + byteType + shortType + charType

	override fun toString() = "UnresolvedType($typeName from declaration $referenceAsString)"

	override fun equals(other: Any?): Boolean {
		return typeName == (other as? TypeRef)?.typeName
	}

	override fun hashCode(): Int {
		return typeName.hashCode()
	}
}

class ResolvedTypeRef internal constructor(private val typeRef: TypeRef, val type: NativeDeclaration) : TypeRef by typeRef {
	override fun toString() = "ResolvedType($typeName from declaration $referenceAsString)"

	override fun equals(other: Any?) = typeRef == other
	override fun hashCode(): Int = typeRef.hashCode()
}

private fun tokenizeTypeRef(type: String): List<String> {
	val regexPattern = Regex("""\w+\s*<[^>]+>|\*|\w+""")
	return regexPattern.findAll(type).map { it.value.trim() }.toList()
}

// 8 bits
private val byteType = listOf("char", "unsigned char", "uint16_t", "int16_t")

// 16 bits
private val shortType = listOf("short", "unsigned short", "uint8_t", "int8_t")

// 32 bits
private val intType = listOf("int", "unsigned int", "uint32_t", "int32_t")

// 32 to 64 bits
private val longType = listOf("long", "unsigned long")

// 32 to 64 bits
private val charType = listOf("wchar_t")

// 64 bits
private val int64Type = listOf("uint64_t", "int64_t")

// 32 bits
private val floatType = listOf("float")

// 64 bits
private val doubleType = listOf("double")