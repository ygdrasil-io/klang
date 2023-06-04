package klang.parser.json.domain

import kotlinx.serialization.json.*
import mu.KotlinLogging

internal data class Node<T>(
	val content: T,
	var children: List<Node<T>> = emptyList(),
) {
	fun notLastOf(sibling: List<Node<T>>): Boolean = sibling.last() != this
}

internal typealias TranslationUnitNode = Node<Pair<TranslationUnitKind, JsonObject>>

internal val TranslationUnitNode.kind: TranslationUnitKind
	get() = content.first

internal val TranslationUnitNode.json: JsonObject
	get() = content.second

private val logger = KotlinLogging.logger {}

internal fun JsonObject.toNode(): TranslationUnitNode = Node(
	kind() to this,
	this["inner"]
		?.jsonArray
		?.mapNotNull { (it as? JsonObject) ?: it.knowNode() }
		?.map { it.toNode() }
		?: emptyList()
)


private fun JsonObject.kind() = (this["kind"]
	?.let(JsonElement::jsonPrimitive)
	?.let(JsonPrimitive::content)
	?.let { TranslationUnitKind.of(it) }
	?: error("no kind: $this"))

private fun JsonElement.knowNode(): JsonObject? {
	logger.error { "unknown node: $this" }
	return null
}
