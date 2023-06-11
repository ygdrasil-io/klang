package klang.parser.ast

import klang.parser.json.domain.Node
import klang.parser.json.domain.TranslationUnitKind
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

const val invalidLocation = "<<invalid sloc>> <invalid sloc>"

internal sealed class FileLocation
internal class InvalidFileLocation : FileLocation()
internal class ValidFileLocation : FileLocation()

internal data class TokenizedTranslationUnit(
	val fileLocation: FileLocation,
	val kind: TranslationUnitKind,
	val payload: List<String>
)

internal typealias TokenizedTranslationUnitNode = Node<TokenizedTranslationUnit>

internal fun List<RawTranslationUnitNode>.tokenize() = map { it.tokenize() }
private fun RawTranslationUnitNode.tokenize(): TokenizedTranslationUnitNode = Node(
	content = content.tokenize(),
	children = children.tokenize()
)

private fun String.tokenize(): TokenizedTranslationUnit {
	var content = this
	val kind = content.findKind() ?: error("No kind found in $this")
	content = content.removeKind()
	val location = content.findLocation()
	content = content.removeLocation(location)
	val tokens = content.split(" ")

	return TokenizedTranslationUnit(
		fileLocation = location,
		kind = kind,
		payload = tokens
	)
}

private fun String.removeLocation(location: FileLocation): String {
	return when (location) {
		is InvalidFileLocation -> substring(invalidLocation.length)
		is ValidFileLocation -> when(substring(0, 1) == "<") {
			true -> substring(indexOf(">") + 1)
			false -> this
		}
	}
}

private fun String.findLocation(): FileLocation {
	return when {
		startsWith(invalidLocation) -> InvalidFileLocation()
		else -> ValidFileLocation()
	}

}

private fun String.removeKind(): String {
	return indexOf(" ")
		.let { substring(it + 1) }
		.let { substring(it.indexOf(" ") + 1) }
}

private fun String.findKind(): TranslationUnitKind? {
	return indexOf(" ")
		.let { substring(0, it) }
		.let { TranslationUnitKind.of(it) }
		.also { if (it == null) logger.info { "kind not found from $this" } }
}
