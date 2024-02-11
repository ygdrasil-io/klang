package klang.parser.libclang

import klang.DeclarationRepository
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

private val logger = KotlinLogging.logger {}

enum class ParserTechnology {
	JNA,
	Panama
}

fun parseFile(
	fileAsString: String,
	filePathAsString: String? = null,
	headerPathsAsString: Array<String> = arrayOf(),
	parserTechnology: ParserTechnology = ParserTechnology.Panama
): DeclarationRepository {

	val fileToParse = computeFile(filePathAsString, fileAsString)
	val path = computePath(filePathAsString)
	val headerPaths = computeHeadersPaths(headerPathsAsString)

	logger.info {
		"will parse file at ${fileToParse.absolutePath} with $parserTechnology and paths ${headerPaths.map { it.toFile().absolutePath }}"
	}

	return parseFile(fileToParse, path, headerPaths, parserTechnology)
}

private fun computeHeadersPaths(headerPathsAsString: Array<String>) =
	headerPathsAsString.map { Path.of(it).also { assert(it.exists()) { "File not found ${it.absolutePathString()}" } } }.toTypedArray()

private fun computePath(filePathAsString: String?) = filePathAsString?.let { Path.of(it) }
	?.also { assert(it.exists()) }

private fun computeFile(filePathAsString: String?, fileAsString: String) = when (filePathAsString != null) {
	true -> filePathAsString.let { "$it/$fileAsString" }
		.let(::File)

	false -> File(fileAsString)
}.also { assert(it.exists()) }

private fun parseFile(
	file: File,
	filePath: Path? = null,
	headerPaths: Array<Path> = arrayOf(),
	parserTechnology: ParserTechnology = ParserTechnology.Panama
) = when (parserTechnology) {

	ParserTechnology.JNA -> {
		assert(filePath == null) { "file path is not supported on JNA" }
		assert(headerPaths.isEmpty()) { "header paths is not supported on JNA" }
		parseFileWithJna(file.absolutePath)
	}

	ParserTechnology.Panama -> parseFileWithPanama(file.absolutePath, filePath, headerPaths)
}