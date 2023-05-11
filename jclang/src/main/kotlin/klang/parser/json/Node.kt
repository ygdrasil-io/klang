package klang.parser.json

import kotlinx.serialization.json.*
import mu.KotlinLogging

data class Node<T>(val content: T, val children: List<Node<T>>) {
	fun notLastOf(sibling: List<Node<T>>): Boolean = sibling.last() != this
}

typealias TranslationUnitNode = Node<Pair<TranslationUnitKind, JsonObject>>

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
