package klang.parser.libclang

import klang.DeclarationRepository
import java.io.File

enum class ParserTechnology {
	JNA,
	Panama
}

fun parseFile(fileAsString: String, parserTechnology: ParserTechnology = ParserTechnology.Panama): DeclarationRepository {
	val file = File(fileAsString)
	assert(file.exists())
	return parseFile(file, parserTechnology)
}

internal fun parseFile(file: File, parserTechnology: ParserTechnology = ParserTechnology.Panama) = when (parserTechnology) {
	ParserTechnology.JNA -> parseFileWithJna(file.absolutePath)
	ParserTechnology.Panama -> parseFileWithPanama(file.absolutePath)
}