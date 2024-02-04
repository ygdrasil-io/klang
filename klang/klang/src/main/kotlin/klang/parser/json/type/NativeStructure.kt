package klang.parser.json.type

import klang.domain.*
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


internal fun TranslationUnitNode.toNativeTypeDefStructure(sibling: TranslationUnitNode) = NativeStructure(
	name = sibling.json.name(),
	fields = this.extractFields(),
	isUnion = json.isUnion()
)

internal fun TranslationUnitNode.toNativeStructure() = NativeStructure(
	name = json.name(),
	fields = this.extractFields(),
	isUnion = json.isUnion()
)

internal fun TranslationUnitNode.isTypeDefStructure(sibling: List<TranslationUnitNode>) =
	isTypeDefStructure(sibling, sibling.indexOf(this))

private fun TranslationUnitNode.isTypeDefStructure(
	sibling: List<TranslationUnitNode>,
	index: Int
): Boolean =
	notLastOf(sibling)
			&& sibling[index + 1].content.first == TranslationUnitKind.TypedefDecl
			&& json.nullableName() == null

private fun TranslationUnitNode.extractFields(): List<StructureField> =
	children.filter { it.content.first == TranslationUnitKind.FieldDecl }
		.map { it.extractField() }


private fun TranslationUnitNode.extractField(): StructureField {
	val name = json.nullableName()
		?: "" // Some field can use empty name to get specific alignment (see: __darwin_fp_control as example)
	val value = json.nullableType()
		?.let(::typeOf)
		?.getOrNull()
		?: error("no type for : $this")
	return TypeRefField(name, value)
}

private fun JsonObject.isUnion(): Boolean {
	return this["tagUsed"]?.jsonPrimitive?.content == "union"
}

