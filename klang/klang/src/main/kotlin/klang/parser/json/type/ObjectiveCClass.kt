package klang.parser.json.type

import arrow.core.Either
import klang.domain.*
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.json.domain.kind
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toObjectiveCClass(): ObjectiveCClass {
	val name = json.name()
	return ObjectiveCClass(
		name = name,
		superType = json.superType(),
		protocols = json.protocols(),
		properties = json.properties(),
		methods = json.methods()
	)
}

private fun JsonObject.protocols(): Set<TypeRef> = this["protocols"]
	?.jsonArray
	?.map { it.jsonObject.get("name")?.jsonPrimitive?.content }
	?.filterNotNull()
	?.map(::typeOf)
	?.mapNotNull(Either<String, TypeRef>::getOrNull)
	?.toSet() ?: setOf()

private fun JsonObject.superType(): TypeRef? = this["super"]
	?.jsonObject
	?.get("name")
	?.jsonPrimitive
	?.content
	?.let(::typeOf)
	?.getOrNull()

private fun JsonObject.methods(): List<ObjectiveCClass.Method> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCMethodDecl }
	?.filter { !(it.nullableBooleanValueOf("isImplicit") ?: false) }
	?.map { it.toMethod() } ?: listOf()

private fun JsonObject.toMethod() = ObjectiveCClass.Method(
	name = name(),
	returnType = returnType(),
	instance = booleanValueOf("instance"),
	arguments = arguments()
)

private fun JsonObject.arguments(): List<ObjectiveCClass.Method.Argument> = inner()
	?.filter { it.kind() == TranslationUnitKind.ParmVarDecl }
	?.map { it.toArgument() } ?: listOf()

private fun JsonObject.toArgument() = ObjectiveCClass.Method.Argument(
	name = name(),
	type = type().let(::typeOf).unchecked("fail to parse type $this")
)

private fun JsonObject.properties(): List<ObjectiveCClass.Property> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCPropertyDecl }
	?.map { it.toProperty() } ?: listOf()

private fun JsonObject.toProperty(): ObjectiveCClass.Property = ObjectiveCClass.Property(
	name = name(),
	type = type(),
	assign = nullableBooleanValueOf("assign"),
	readwrite = nullableBooleanValueOf("readwrite"),
	nonatomic = nullableBooleanValueOf("nonatomic"),
	unsafe_unretained = nullableBooleanValueOf("unsafe_unretained")
)

