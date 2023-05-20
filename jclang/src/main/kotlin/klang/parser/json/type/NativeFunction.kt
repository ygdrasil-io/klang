package klang.parser.json.type

import klang.domain.NativeFunction
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeFunction() = NativeFunction(
	name = json.functionName(),
	returnType = json.returnType(),
	arguments = arguments()
)

private fun TranslationUnitNode.arguments(): List<Pair<String, String>> =
		children.filter { it.content.first == TranslationUnitKind.ParmVarDecl }
			.map { it.extractArguments() }

private fun TranslationUnitNode.extractArguments(): Pair<String, String> {
	val name = json["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val type = json["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
		?: error("no type for : $this")
	return name to type
}

private fun JsonObject.returnType() = this["type"]
	?.jsonObject?.get("qualType")?.jsonPrimitive?.content
	?.let { it.substring(0, it.indexOf("(")) }
	?.trim()
	?: error("no return type: $this")


private fun JsonObject.nullableFunctionName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.functionName() = nullableFunctionName()
	?: error("no enumeration name: $this")
