import klang.parser.json.ParserRepository
import klang.parser.json.parseAstJson
import klang.parser.libclang.parseFile
import mu.KotlinLogging

const val clangHeaderPath = "clang/14.0.0/clang-c/Index.h"
const val clangJsonDumpPath = "clang/clang.14.0.0.ast.json"

private val logger = KotlinLogging.logger {}

fun main() {

	parseAstJson(clangJsonDumpPath)
	//parseFile(clangHeaderPath)


	logger.info { "parsing ended with ${ParserRepository.errors.size} errors" }
	ParserRepository.errors.forEach { logger.error { it } }
}