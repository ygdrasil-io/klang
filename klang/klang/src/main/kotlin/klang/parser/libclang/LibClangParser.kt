package klang.parser.libclang

import klang.DeclarationRepository
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

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
	val file = when (filePathAsString != null) {
		true -> filePathAsString.let { "$it/$fileAsString" }
			.let(::File)
		false -> File(fileAsString)
	}.also { assert(it.exists()) }
	val path = filePathAsString?.let { Path.of(it) }
		?.also { assert(it.exists())  }
	val headerPaths = headerPathsAsString.map { Path.of(it).also { assert(it.exists()) } }.toTypedArray()
	return parseFile(file, path, headerPaths, parserTechnology)
}

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