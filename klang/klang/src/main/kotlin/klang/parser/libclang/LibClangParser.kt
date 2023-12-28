package klang.parser.libclang

enum class ParserTechnology {
	JNA,
	Panama
}

fun parseFile(file: String, parserTechnology: ParserTechnology = ParserTechnology.Panama) = when (parserTechnology) {
	ParserTechnology.JNA -> parseFileWithJna(file)
	ParserTechnology.Panama -> parseFileWithPanama(file)
}