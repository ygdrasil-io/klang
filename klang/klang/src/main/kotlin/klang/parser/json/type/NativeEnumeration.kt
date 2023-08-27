package klang.parser.json.type

import klang.domain.AnonymousEnumeration
import klang.domain.NativeEnumeration
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
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
		.map { it.extractField() }
		.let { list ->
			list
				.mapIndexed { index, (name, value) ->
					when (value) {
						null -> when (index) {
							0 -> name to 0L
							else -> name to (list[index - 1].second?.plus(1) ?: error("no previous value"))
						}
						else -> {
							name to value
						}
					}
				}
		}


private fun TranslationUnitNode.extractField(): Pair<String, Long?> {
	val name = json["name"]?.jsonPrimitive?.content
		?: error("no name for : $this")
	val value = children.firstOrNull { it.content.first == TranslationUnitKind.ConstantExpr }
		?.content?.second?.get("value")?.jsonPrimitive?.longOrNull
	return name to value
}

private fun JsonObject.nullableTypeAlias() = this["name"]?.jsonPrimitive?.content

private fun JsonObject.typeAliasName() = nullableTypeAlias()
	?: AnonymousEnumeration
