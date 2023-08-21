package klang.parser.json.type

import klang.domain.UnresolvedTypeRef
import klang.domain.typeOf
import kotlinx.serialization.json.*

internal fun JsonObject.inner() = this["inner"]
	?.jsonArray
	?.map { it.jsonObject }

internal fun JsonObject.returnType() = nullableReturnType()
	?: error("return type not found: $this")

internal fun JsonObject.type() = nullableType()
	?: error("type not found: $this")

internal fun JsonObject.name() = nullableName()
	?: error("name not found: $this")

internal fun JsonObject.booleanValueOf(key: String)
	= nullableBooleanValueOf(key) ?: error("fail to find boolean with key $key")

internal fun JsonObject.nullableBooleanValueOf(key: String)
	= this[key]?.jsonPrimitive?.booleanOrNull
internal fun JsonObject.nullableReturnType() = this["returnType"]?.jsonObject?.desugaredOrType()?.jsonPrimitive?.content
	?.let(::typeOf)
	?.getOrNull()
internal fun JsonObject.nullableType() = this["type"]?.jsonObject?.desugaredOrType()?.jsonPrimitive?.content
internal fun JsonObject.nullableName() = this["name"]?.jsonPrimitive?.content

//TODO delete commented code and inline function if that really not needed
internal fun JsonObject.desugaredOrType() = /*this["desugaredQualType"] ?: */this["qualType"]