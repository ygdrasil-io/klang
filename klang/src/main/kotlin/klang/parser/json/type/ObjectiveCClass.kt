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
		methods = json.methods()
	)
}

private fun JsonObject.methods(): List<ObjectiveCClass.Method> = inner()
	?.map { it.jsonObject }
	?.filter { it.kind() == TranslationUnitKind.ObjCMethodDecl }
	?.map { it.toMethod() } ?: listOf()

private fun JsonObject.toMethod() = ObjectiveCClass.Method(
	name = name(),
	returnType = returnType(),
	instance = booleanValueOf("instance"),
	arguments = arguments()
)

private fun JsonObject.arguments(): List<ObjectiveCClass.Method.Argument> = inner()
	?.map { it.jsonObject }
	?.map { it.toArgument() } ?: listOf()

private fun JsonObject.toArgument() = ObjectiveCClass.Method.Argument(
	name = name(),
	type = type()
)

private fun JsonObject.properties(): List<ObjectiveCClass.Property> = inner()
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

