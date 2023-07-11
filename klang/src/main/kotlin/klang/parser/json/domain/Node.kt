package klang.parser.json.domain

import kotlinx.serialization.json.*
import mu.KotlinLogging

data class Node<T>(val content: T, val children: List<Node<T>>) {
	fun notLastOf(sibling: List<Node<T>>): Boolean = sibling.last() != this
}

typealias TranslationUnitNode = Node<Pair<TranslationUnitKind, JsonObject>>

val TranslationUnitNode.kind: TranslationUnitKind
	get() = content.first

val TranslationUnitNode.json: JsonObject
	get() = content.second

private val logger = KotlinLogging.logger {}

internal fun JsonObject.toNode(): TranslationUnitNode = Node(
	kind() to this,
	this["inner"]
		?.jsonArray
		?.mapNotNull { (it as? JsonObject) ?: it.unknownNode() }
		?.map { it.toNode() }
		?: emptyList()
)


internal fun JsonObject.kind() = (this["kind"]
	?.let(JsonElement::jsonPrimitive)
	?.let(JsonPrimitive::content)
	?.let { TranslationUnitKind.of(it) }
	?: error("no kind: $this"))

private fun JsonElement.unknownNode(): JsonObject? {
	logger.error { "unknown node: $this" }
	return null
}
