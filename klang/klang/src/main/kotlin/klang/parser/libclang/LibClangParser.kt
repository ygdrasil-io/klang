package klang.parser.libclang

import klang.DeclarationRepository
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

private val logger = KotlinLogging.logger {}

fun DeclarationRepository.parseFile(
	fileAsString: String,
	filePathAsString: String? = null,
	headerPathsAsString: Array<String> = arrayOf(),
	macros: Map<String, String?> = mapOf()
): DeclarationRepository {

	val fileToParse = computeFile(filePathAsString, fileAsString)
	val path = computePath(filePathAsString)
	val headerPaths = computeHeadersPaths(headerPathsAsString)

	logger.info {
		"will parse file at ${fileToParse.absolutePath} and paths ${headerPaths.map { it.toFile().absolutePath }}"
	}

	return parseFile(fileToParse, path, headerPaths, macros)
}

private fun computeHeadersPaths(headerPathsAsString: Array<String>) =
	headerPathsAsString.map { Path.of(it).also { check(it.exists()) { "File not found ${it.absolutePathString()}" } } }.toTypedArray()

private fun computePath(filePathAsString: String?) = filePathAsString?.let { Path.of(it) }
	?.also { check(it.exists()) }

private fun computeFile(filePathAsString: String?, fileAsString: String) = when (filePathAsString != null) {
	true -> filePathAsString.let { "$it/$fileAsString" }
		.let(::File)

	false -> File(fileAsString)
}.also { check(it.exists()) }

private fun DeclarationRepository.parseFile(
	file: File,
	filePath: Path? = null,
	headerPaths: Array<Path>,
	macros: Map<String, String?>
) = parseFileWithPanama(file.absolutePath, filePath, headerPaths, macros)