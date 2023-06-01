package klang.parser.json.type

import klang.domain.NativeTypeAlias
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeTypeAlias()= NativeTypeAlias(
        name = json.typeAliasName(),
        type = json.type()
)

private fun JsonObject.type(): String = this["type"]
        ?.jsonObject
        ?.get("qualType")
        ?.jsonPrimitive
        ?.content
        ?: error("no type for : $this")

private fun JsonObject.nullableTypeAlias() = this["name"]
        ?.jsonPrimitive
        ?.content

private fun JsonObject.typeAliasName() = nullableTypeAlias()
        ?: error("no enumeration name: $this")