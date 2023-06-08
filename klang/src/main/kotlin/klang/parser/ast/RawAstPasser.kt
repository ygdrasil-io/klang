package klang.parser.ast

import klang.parser.json.domain.Node
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.File

internal typealias RawTranslationUnitNode = Node<String>

private val logger = KotlinLogging.logger {}

private class RawAstPasserContext(
	val buffer: BufferedReader,
	val nodes: MutableList<RawTranslationUnitNode> = mutableListOf(),
	val stack: MutableList<RawTranslationUnitNode> = mutableListOf()
) {
	fun insertIntoStack(node: Node<String>, position: Int) {
		val index = position - 1
		if (stack.size < position) {
			stack.add(node)
		} else {
			stack[index] = node
		}

		if(position > 1) {
			stack[index - 1].children = stack[index - 1].children + listOf(node)
		}
	}
}

internal fun rawAst(astStream: File) : Result<List<RawTranslationUnitNode>> = runCatching {
	astStream.bufferedReader().use { buffer ->
		buffer.checkInitialLine()
		return RawAstPasserContext(buffer).apply {
			while (buffer.ready()) {
				buffer.readLine()
					.toNode(this)
					?.let(nodes::add)
			}
		}
			.nodes
			.toResult()
	}
}

private fun String.toNode(context: RawAstPasserContext): RawTranslationUnitNode? {
	val position = getPositionOnHierarchy() ?: return null
	val content = contentFromPosition(position)
	val node = RawTranslationUnitNode(content)
	context.insertIntoStack(node, position)
	logger.info { "at $position $content" }

	return when (position) {
		1 -> node
		else -> null
	}
}

private fun String.contentFromPosition(position: Int) = substring(position * 2)

internal fun String.getPositionOnHierarchy(): Int? {
	return indexOf("-")
		.takeIf { it != -1 }
		?.let { (it + 1) / 2 }
}

private fun MutableList<RawTranslationUnitNode>.toResult(): Result<List<RawTranslationUnitNode>>
	= Result.success(this)

private fun BufferedReader.checkInitialLine() {
	if (readLine().startsWith("TranslationUnitDecl").not()) {
		Error("Invalid initial line")
	}
}
