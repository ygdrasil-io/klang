package klang.tools

import mu.KotlinLogging
import java.io.File

class CommandRunner internal constructor(
	command: String,
	arguments: List<String> = emptyList(),
	outputFile: File? = null,
	errorOutputFile: File? = null
) {
	private val logger = KotlinLogging.logger {}
	val outputFile: File = outputFile ?: File.createTempFile("klang", ".output")
		.also(File::deleteOnExit)
	val errorOutputFile: File = errorOutputFile ?: File.createTempFile("klang", ".error.output")
		.also(File::deleteOnExit)
	private val finalCommand = "$command ${arguments.joinToString(" ")}"
	var exitCode = 0
		private set
	internal fun run() {
		logger.info { "will run command: $finalCommand" }
		ProcessBuilder()
			.command("bash", "-c", finalCommand)
			.redirectOutput(outputFile)
			.redirectError(errorOutputFile)
			.start().apply {
				waitFor()
				exitCode = exitValue()
			}
		logger.info { "process exit with code $exitCode" }
		logger.info { "output file: ${outputFile.absolutePath}" }
		logger.info { "error output file: ${errorOutputFile.absolutePath}" }
	}
}

fun String.runCommand(arguments: List<String> = emptyList(), outputFile: File? = null) =
	CommandRunner(this, arguments, outputFile)
		.also(CommandRunner::run)
