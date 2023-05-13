#!/usr/bin/env kotlinc -script -J-Xmx2g

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val files = listOf(
	"typedef-enum.h" to "typedef-enum.h.ast.json",
	"enum.h" to "enum.h.ast.json",
	"struct.h" to "struct.h.ast.json",
	"typedef-struct.h " to "typedef-struct.h.ast.json",
	"functions.h " to "functions.h.ast.json"
)

files
	.filter { (source, _) -> source.let(::File).exists().not() }
	.forEach { (source, output) ->
		"clang -Xclang -ast-dump=json -fsyntax-only -I./ ./$source > ./$output".run {
			println("failed to dump ast for $source with error:\n$this")
		}
	}

fun String.run(onError: (String) -> Unit) {
	val process = ProcessBuilder()
		.command("bash", "-c", this)
		.start()

	val reader = BufferedReader(InputStreamReader(process.errorStream))
	val errors = mutableListOf<String>()
	var line: String?
	while (reader.readLine().also { line = it } != null) {
		errors.add(line ?: "")
	}

	if (errors.isNotEmpty()) {
		onError(errors.joinToString("\n"))
	}
}