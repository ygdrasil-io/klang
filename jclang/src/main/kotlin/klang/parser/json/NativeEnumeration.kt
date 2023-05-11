package klang.parser.json

import klang.domain.NativeEnumeration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

internal fun TranslationUnitNode.toNativeTypeDefEnumeration(sibling: TranslationUnitNode) = NativeEnumeration(
	name = sibling.content.second.enumerationName(),
	values = this.extractValues()
)

internal fun TranslationUnitNode.toNativeEnumeration() = NativeEnumeration(
	name = content.second.enumerationName(),
	values = this.extractValues()
)

internal fun TranslationUnitNode.isTypeDefEnumeration(sibling: List<TranslationUnitNode>) =
	isTypeDefEnumeration(sibling, sibling.indexOf(this))

private fun TranslationUnitNode.isTypeDefEnumeration(
	sibling: List<TranslationUnitNode>,
	index: Int
): Boolean =
	notLastOf(sibling)
			&& sibling[index + 1].content.first == TranslationUnitKind.TypedefDecl
			&& content.second.nullableEnumerationName() == null

private fun TranslationUnitNode.extractValues(): List<Pair<String, Int>> =
	children.filter { it.content.first == TranslationUnitKind.EnumConstantDecl }
		.map { it.extractValue(children) }


private fun TranslationUnitNode.extractValue(sibling: List<TranslationUnitNode>): Pair<String, Int> {
	val name = content.second["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val value = children.firstOrNull { it.content.first == TranslationUnitKind.ConstantExpr }
		?.content?.second?.get("value")?.jsonPrimitive?.intOrNull
		?: sibling.filter { it.content.first == content.first }.indexOf(this)
	return name to value
}

private fun JsonObject.nullableEnumerationName() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.enumerationName() = nullableEnumerationName()
	?: error("no enumeration name: $this")
