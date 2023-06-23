package klang.parser.json.type

import klang.domain.NativeDeclaration
import klang.domain.ObjectiveCClass
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toObjectiveCClass(): ObjectiveCClass? {
	return ObjectiveCClass(
		name = json.name(),
		properties = listOf(),
		methods = listOf()
	)
}

private fun JsonObject.nullableName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.name() = nullableName()
	?: error("no enumeration name: $this")