package klang.parser.json.type

import klang.domain.NativeStructure
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


internal fun TranslationUnitNode.toNativeTypeDefStructure(sibling: TranslationUnitNode) = NativeStructure(
	name = sibling.json.enumerationName(),
	fields = this.extractFields()
)

internal fun TranslationUnitNode.toNativeStructure() = NativeStructure(
	name = json.enumerationName(),
	fields = this.extractFields()
)

internal fun TranslationUnitNode.isTypeDefStructure(sibling: List<TranslationUnitNode>) =
	isTypeDefStructure(sibling, sibling.indexOf(this))

private fun TranslationUnitNode.isTypeDefStructure(
	sibling: List<TranslationUnitNode>,
	index: Int
): Boolean =
	notLastOf(sibling)
			&& sibling[index + 1].content.first == TranslationUnitKind.TypedefDecl
			&& json.nullableEnumerationName() == null

private fun TranslationUnitNode.extractFields(): List<Pair<String, String>> =
	children.filter { it.content.first == TranslationUnitKind.FieldDecl }
		.map { it.extractField() }


private fun TranslationUnitNode.extractField(): Pair<String, String> {
	val name = json["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val value = json["type"]?.jsonObject?.get("qualType")?.jsonPrimitive?.content
		?: error("no type for : $this")
	return name to value
}

private fun JsonObject.nullableEnumerationName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.enumerationName() = nullableEnumerationName()
	?: error("no enumeration name: $this")
