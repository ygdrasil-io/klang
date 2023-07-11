package klang.parser.json.type

import kotlinx.serialization.json.*

internal fun JsonObject.inner() = this["inner"]
	?.jsonArray

internal fun JsonObject.returnType() = nullableReturnType()
	?: error("return type not found: $this")

internal fun JsonObject.type() = nullableType()
	?: error("type not found: $this")

internal fun JsonObject.name() = nullableName()
	?: error("name not found: $this")

internal fun JsonObject.booleanValueOf(key: String)
	= this[key]?.jsonPrimitive?.booleanOrNull ?: error("fail to find boolean with key $key")
internal fun JsonObject.nullableReturnType() = this["returnType"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
internal fun JsonObject.nullableType() = this["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
internal fun JsonObject.nullableName() = this["name"]?.jsonPrimitive?.content