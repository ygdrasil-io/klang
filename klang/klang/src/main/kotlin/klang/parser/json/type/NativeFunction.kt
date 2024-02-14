package klang.parser.json.type

import klang.domain.*
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeFunction() = NativeFunction(
	name = NotBlankString(json.functionName()),
	returnType = typeOf(json.type().adapt()).unchecked("fail to create type ${json.type()}"),
	arguments = arguments()
)

private fun TranslationUnitNode.arguments() =
		children.filter { it.content.first == TranslationUnitKind.ParmVarDecl }
			.map { it.extractArguments() }

private fun TranslationUnitNode.extractArguments(): NativeFunction.Argument {
	val name = json.nullableName() ?: ""
	val type = json.nullableType()
		?: error("no type for : $this")
	return NativeFunction.Argument(notBlankString(name), typeOf(type).unchecked("fail to create type $type"))
}

private fun String.adapt() = let { it.substring(0, it.indexOf("(")) }
	.trim()

private fun JsonObject.nullableFunctionName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.functionName() = nullableFunctionName()
	?: error("no enumeration name: $this")
