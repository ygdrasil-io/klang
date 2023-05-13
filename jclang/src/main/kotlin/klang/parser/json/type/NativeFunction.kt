package klang.parser.json.type

import klang.domain.NativeFunction
import klang.parser.json.domain.TranslationUnitNode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeFunction() = NativeFunction(
	name = content.second.functionName(),
	returnType = content.second.returnType(),
	arguments = listOf()
)

private fun JsonObject.returnType() = this["type"]
	?.jsonObject?.get("qualType")?.jsonPrimitive?.content
	?.let { it.substring(0, it.indexOf("(") - 1) }
	?: error("no return type: $this")


private fun JsonObject.nullableFunctionName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.functionName() = nullableFunctionName()
	?: error("no enumeration name: $this")
