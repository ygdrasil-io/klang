package klang.parser.json.type

import klang.domain.ObjectiveCClass
import klang.domain.TypeRef
import klang.domain.UnresolvedTypeRef
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.json.domain.kind
import kotlinx.serialization.json.*

internal fun TranslationUnitNode.toObjectiveCClass(): ObjectiveCClass {
	return ObjectiveCClass(
		name = json.name(),
		superType = json.superType(),
		protocols = json.protocols(),
		properties = json.properties(),
		methods = json.methods()
	)
}

private fun JsonObject.protocols(): Set<UnresolvedTypeRef> = this["protocols"]
	?.jsonArray
	?.map { it.jsonObject.get("name")?.jsonPrimitive?.content }
	?.filterNotNull()
	?.map(::UnresolvedTypeRef)
	?.toSet() ?: setOf()

private fun JsonObject.superType(): TypeRef = this["super"]
	?.jsonObject
	?.get("name")
	?.jsonPrimitive
	?.content
	?.let(::UnresolvedTypeRef) ?: error("fail to find supertype")

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
	?.map { it.toArgument() } ?: listOf()

private fun JsonObject.toArgument() = ObjectiveCClass.Method.Argument(
	name = name(),
	type = type()
)

private fun JsonObject.properties(): List<ObjectiveCClass.Property> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCPropertyDecl }
	?.map { it.toProperty() } ?: listOf()

private fun JsonObject.toProperty(): ObjectiveCClass.Property = ObjectiveCClass.Property(
	name = name(),
	type = type(),
	assign = booleanValueOf("assign"),
	readwrite = booleanValueOf("readwrite"),
	nonatomic = booleanValueOf("nonatomic"),
	unsafe_unretained = booleanValueOf("unsafe_unretained")
)

