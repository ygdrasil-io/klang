package klang.parser.json.type

import klang.domain.*
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.json.domain.kind
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toObjectiveCProtocol(): ObjectiveCProtocol {
	return ObjectiveCProtocol(
		name = NotBlankString(json.name()),
		protocols = json.protocols(),
		properties = json.properties(),
		methods = json.methods()
	)
}

private fun JsonObject.protocols(): Set<String> = this["protocols"]
	?.jsonArray
	?.map { it.jsonObject.get("name")?.jsonPrimitive?.content }
	?.filterNotNull()
	?.toSet() ?: setOf()

private fun JsonObject.methods(): List<ObjectiveCClass.Method> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCMethodDecl }
	?.filter { !(it.nullableBooleanValueOf("isImplicit") ?: false) }
	?.map { it.toMethod() } ?: listOf()

private fun JsonObject.toMethod() = ObjectiveCClass.Method(
	name = NotBlankString(name()),
	returnType = returnType(),
	instance = booleanValueOf("instance"),
	arguments = arguments()
)

private fun JsonObject.arguments(): List<ObjectiveCClass.Method.Argument> = inner()
	?.filter { it.kind() == TranslationUnitKind.ParmVarDecl }
	?.map { it.toArgument() } ?: listOf()

private fun JsonObject.toArgument() = ObjectiveCClass.Method.Argument(
	name = name(),
	type = type().let(::typeOf).unchecked("fail to parse")
)

private fun JsonObject.properties(): List<ObjectiveCClass.Property> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCPropertyDecl }
	?.map { it.toProperty() } ?: listOf()

private fun JsonObject.toProperty(): ObjectiveCClass.Property = ObjectiveCClass.Property(
	name = NotBlankString(name()),
	type = type(),
	assign = nullableBooleanValueOf("assign"),
	readwrite = nullableBooleanValueOf("readwrite"),
	nonatomic = nullableBooleanValueOf("nonatomic"),
	unsafe_unretained = nullableBooleanValueOf("unsafe_unretained")
)

