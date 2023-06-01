package klang.tools

import mu.KotlinLogging
import java.io.File
import java.nio.file.Files

private const val dockerImage = "ubuntu-clang-16"
private const val baseContainerDirectory = "/workspace"
private val logger = KotlinLogging.logger {}

fun generateAstFromDocker(
	sourcePath: String,
	sourceFile: String,
	clangAstOutput: File? = null,
	clangJsonAstOutput: File? = null,
	clangQueryAstOutput: File? = null,
) {
	val dockerCommand = DockerCommandBuilder(
		dockerImage = dockerImage,
		directoryBindings = listOf(
			sourcePath to baseContainerDirectory
		)
	).build()

	clangQueryAstOutput?.apply {
		generateAstFromClangQuery(dockerCommand, sourceFile, this)
	}

	clangAstOutput?.apply {
		generateAstFromClang(dockerCommand, sourceFile, this)
	}

	clangJsonAstOutput?.apply {
		generateJsonAstFromClang(dockerCommand, sourceFile, this)
	}


}

private fun generateJsonAstFromClang(dockerCommand: List<String>, sourceFile: String, clangJsonAstOutput: File) {
	"docker".runCommand(
		arguments = dockerCommand + listOf("clang-16", "-Xclang", "-ast-dump=json", "-fsyntax-only", "-I", "$baseContainerDirectory", "$baseContainerDirectory/$sourceFile"),
		clangJsonAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}
}

private fun generateAstFromClang(dockerCommand: List<String>, sourceFile: String, clangAstOutput: File) {
	"docker".runCommand(
		arguments = dockerCommand + listOf("clang-16", "-Xclang", "-ast-dump", "-fsyntax-only", "-I", "$baseContainerDirectory", "$baseContainerDirectory/$sourceFile"),
		clangAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}
}

private fun generateAstFromClangQuery(dockerCommand: List<String>, sourceFile: String, clangQueryAstOutput: File) {
	val clangQueryCommand = ClangQueryCommandBuilder(
		buildPath = File(baseContainerDirectory),
		sourcePath = File("$baseContainerDirectory/$sourceFile"),
	).build()

	"docker".runCommand(
		arguments = dockerCommand + clangQueryCommand,
		clangQueryAstOutput
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}
}
