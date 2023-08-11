package klang.parser.json.type

import klang.domain.NativeFunction
import klang.domain.typeOf
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeFunction() = NativeFunction(
	name = json.functionName(),
	returnType = typeOf(json.type().adapt()).getOrNull() ?: error("fail to create type ${json.type()}"),
	arguments = arguments()
)

private fun TranslationUnitNode.arguments() =
		children.filter { it.content.first == TranslationUnitKind.ParmVarDecl }
			.map { it.extractArguments() }

private fun TranslationUnitNode.extractArguments(): NativeFunction.Argument {
	val name = json["name"]?.jsonPrimitive?.content
	val type = json["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
		?: error("no type for : $this")
	return NativeFunction.Argument(name, typeOf(type).getOrNull() ?: error("fail to create type $type"))
}

private fun String.adapt() = let { it.substring(0, it.indexOf("(")) }
	.trim()

private fun JsonObject.nullableFunctionName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.functionName() = nullableFunctionName()
	?: error("no enumeration name: $this")
