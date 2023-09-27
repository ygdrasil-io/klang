package klang.parser.json.type

import klang.domain.*
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.json.domain.kind
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toObjectiveCCategory(): ObjectiveCCategory {
	return ObjectiveCCategory(
		name = json.nullableName() ?: AnonymousCategoryName,
		superType = json.superType(),
		methods = json.methods()
	)
}

private fun JsonObject.superType() = this["interface"]
	?.jsonObject
	?.get("name")
	?.jsonPrimitive
	?.content
	?.let(::typeOf)
	?.getOrNull()
	?: error("fail to find supertype")

private fun JsonObject.methods(): List<ObjectiveCClass.Method> = inner()
	?.filter { it.kind() == TranslationUnitKind.ObjCMethodDecl }
	?.filter { !(it.nullableBooleanValueOf("isImplicit") ?: false) }
	?.map { it.toMethod() } ?: listOf()

private fun JsonObject.toMethod() = ObjectiveCClass.Method(
	name = name(),
	returnType = returnType(),
	instance = booleanValueOf("instance"),
	arguments = arguments()
)

private fun JsonObject.arguments(): List<ObjectiveCClass.Method.Argument> = inner()
	?.filter { it.kind() == TranslationUnitKind.ParmVarDecl }
	?.map { it.toArgument() } ?: listOf()

private fun JsonObject.toArgument() = ObjectiveCClass.Method.Argument(
	name = name(),
	type = type().let(::typeOf).unchecked("fail to parse type $this")
)
