package klang.parser.json.type

import klang.domain.ObjectiveCClass
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.json.domain.kind
import kotlinx.serialization.json.*

internal fun TranslationUnitNode.toObjectiveCClass(): ObjectiveCClass? {
	return ObjectiveCClass(
		name = json.name(),
		properties = json.properties(),
		methods = listOf()
	)
}

private fun JsonObject.properties(): List<ObjectiveCClass.Property> = this["inner"]
	?.jsonArray
	?.map { it.jsonObject }
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

private fun JsonObject.type() = nullableType()
	?: error("type found: $this")

private fun JsonObject.name() = nullableName()
	?: error("name found: $this")

private fun JsonObject.booleanValueOf(key: String)
	= this[key]?.jsonPrimitive?.booleanOrNull ?: error("fail to find boolean with key $key")
private fun JsonObject.nullableType() = this["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
private fun JsonObject.nullableName() = this["name"]?.jsonPrimitive?.content