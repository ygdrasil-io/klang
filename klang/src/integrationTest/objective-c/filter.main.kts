#!/usr/bin/env kotlinc -script

/**
 * Use this script to filter the AST of a file and debug more easily
 */

import java.io.File

"jq".runCommand(
	listOf("'.inner | map(select(.id == \"0x7fe0b829ac88\"))'", "./foundation.m.ast.json"),
	File("result.json"),
	File("error.txt")
)

class CommandRunner internal constructor(
	command: String,
	arguments: List<String> = emptyList(),
	outputFile: File? = null,
	errorOutputFile: File? = null
) {

	val outputFile: File = outputFile ?: File.createTempFile("klang", ".output")
		.also(File::deleteOnExit)
	val errorOutputFile: File = errorOutputFile ?: File.createTempFile("klang", ".error.output")
		.also(File::deleteOnExit)
	private val finalCommand = "$command ${arguments.joinToString(" ")}"
	var exitCode = 0
		private set
	internal fun run() {
		println("will run command: $finalCommand")
		ProcessBuilder()
			.command("bash", "-c", finalCommand)
			.redirectOutput(outputFile)
			.redirectError(errorOutputFile)
			.start().apply {
				waitFor()
				exitCode = exitValue()
			}
		println( "process exit with code $exitCode" )
		println( "output file: ${outputFile.absolutePath}" )
		println( "error output file: ${errorOutputFile.absolutePath}" )
	}
}

fun String.runCommand(arguments: List<String> = emptyList(), outputFile: File? = null, errorOutputFile: File? = null) =
	CommandRunner(this, arguments, outputFile, errorOutputFile)
		.also(CommandRunner::run)
