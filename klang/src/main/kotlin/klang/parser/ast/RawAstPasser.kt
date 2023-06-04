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
)

internal fun rawAst(astStream: File) : Result<List<RawTranslationUnitNode>> = runCatching {
	astStream.bufferedReader().use { buffer ->
		buffer.checkInitialLine()
		return RawAstPasserContext(buffer).apply {
			while (buffer.ready()) {
				buffer.readLine()
					.toNode()
					?.let(nodes::add)
			}
		}.nodes
			.toResult()
	}
}

private fun String.toNode(): RawTranslationUnitNode? {
	val position = getPositionOnHierarchy()
	val nodes = RawTranslationUnitNode(this)
	logger.info { "at $position $this" }
	TODO("Not yet implemented")
}

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
