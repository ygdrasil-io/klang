package klang.parser.json.type

import klang.domain.NativeEnumeration
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeTypeDefEnumeration(sibling: TranslationUnitNode) = NativeEnumeration(
	name = sibling.json.enumerationName(),
	values = this.extractFields()
)

internal fun TranslationUnitNode.toNativeEnumeration() = NativeEnumeration(
	name = json.enumerationName(),
	values = this.extractFields()
)

internal fun TranslationUnitNode.isTypeDefEnumeration(sibling: List<TranslationUnitNode>) =
	isTypeDefStructure(sibling, sibling.indexOf(this))

private fun TranslationUnitNode.isTypeDefStructure(
	sibling: List<TranslationUnitNode>,
	index: Int
): Boolean =
	notLastOf(sibling)
			&& sibling[index + 1].content.first == TranslationUnitKind.TypedefDecl
			&& json.nullableEnumerationName() == null

private fun TranslationUnitNode.extractFields(): List<Pair<String, Int>> =
	children.filter { it.content.first == TranslationUnitKind.EnumConstantDecl }
		.map { it.extractField(children) }


private fun TranslationUnitNode.extractField(sibling: List<TranslationUnitNode>): Pair<String, Int> {
	val name = json["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val value = children.firstOrNull { it.content.first == TranslationUnitKind.ConstantExpr }
		?.content?.second?.get("value")?.jsonPrimitive?.intOrNull
		?: sibling.filter { it.content.first == content.first }.indexOf(this)
	return name to value
}

private fun JsonObject.nullableEnumerationName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.enumerationName() = nullableEnumerationName()
	?: error("no enumeration name: $this")
