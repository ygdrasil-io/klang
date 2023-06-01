package klang.parser.json.type

import klang.domain.NativeEnumeration
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

internal fun TranslationUnitNode.toNativeTypeDefEnumeration(sibling: TranslationUnitNode) = NativeEnumeration(
	name = sibling.json.typeAliasName(),
	values = this.extractFields()
)

internal fun TranslationUnitNode.toNativeEnumeration() = NativeEnumeration(
	name = json.typeAliasName(),
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
			&& json.nullableTypeAlias() == null

private fun TranslationUnitNode.extractFields(): List<Pair<String, Long>> =
	children.filter { it.content.first == TranslationUnitKind.EnumConstantDecl }
		.map { it.extractField(children) }


private fun TranslationUnitNode.extractField(sibling: List<TranslationUnitNode>): Pair<String, Long> {
	val name = json["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val value = children.firstOrNull { it.content.first == TranslationUnitKind.ConstantExpr }
		?.content?.second?.get("value")?.jsonPrimitive?.longOrNull
		?: sibling.filter { it.content.first == content.first }.indexOf(this).toLong()
	return name to value
}

private fun JsonObject.nullableTypeAlias() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.typeAliasName() = nullableTypeAlias()
	?: error("no enumeration name: $this")
