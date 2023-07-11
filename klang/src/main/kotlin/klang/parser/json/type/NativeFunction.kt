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
	returnType = json.type().adapt(),
	arguments = arguments()
)

private fun TranslationUnitNode.arguments() =
		children.filter { it.content.first == TranslationUnitKind.ParmVarDecl }
			.map { it.extractArguments() }

private fun TranslationUnitNode.extractArguments(): NativeFunction.Argument {
	val name = json["name"]?.jsonPrimitive?.content
	val type = json["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
		?: error("no type for : $this")
	return NativeFunction.Argument(name,  type)
}

private fun String.adapt() = let { it.substring(0, it.indexOf("(")) }
	.trim()

private fun JsonObject.nullableFunctionName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.functionName() = nullableFunctionName()
	?: error("no enumeration name: $this")
