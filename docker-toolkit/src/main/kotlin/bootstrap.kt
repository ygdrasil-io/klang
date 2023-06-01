import klang.tools.ClangQueryCommandBuilder
import klang.tools.runCommand
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files


val files = listOf(
	"typedef-enum.h" to "typedef-enum.h.ast",
	"enum.h" to "enum.h.ast",
	"struct.h" to "struct.h.ast",
	"typedef-struct.h " to "typedef-struct.h.ast",
	"functions.h " to "functions.h.ast",
	"typedef.h " to "typedef.h.ast"
)

const val baseContainerDirectory = "/workspace"
const val dockerImage = "ubuntu-clang-16"
private val logger = KotlinLogging.logger {}


val sourcePath = File(".")
	.resolve("clang")
	.resolve("14.0.0")
val clangQueryAstOutput = sourcePath
	.resolve("clang-query-ast.log")
val clangAstOutput = sourcePath
	.resolve("clang-ast.log")
val clangJsonAstOutput = sourcePath
	.resolve("clang-ast.json")

fun main() {

	val dockerCommand = listOf(
		"run", "--rm", "--mount", "src=\"${sourcePath.absolutePath}\",target=$baseContainerDirectory,type=bind", dockerImage
	)

	val clangQueryCommand = ClangQueryCommandBuilder(
		buildPath = File(baseContainerDirectory),
		sourcePath = File("$baseContainerDirectory/clang-c/Index.h"),
	).build()

	"docker".runCommand(
		arguments = dockerCommand + clangQueryCommand,
		clangQueryAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}

	"docker".runCommand(
		arguments = dockerCommand + listOf("clang-16", "-Xclang", "-ast-dump", "-fsyntax-only", "-I", "$baseContainerDirectory", "$baseContainerDirectory/clang-c/Index.h"),
		clangAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}

	"docker".runCommand(
		arguments = dockerCommand + listOf("clang-16", "-Xclang", "-ast-dump=json", "-fsyntax-only", "-I", "$baseContainerDirectory", "$baseContainerDirectory/clang-c/Index.h"),
		clangJsonAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}
}

