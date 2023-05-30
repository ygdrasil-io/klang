#!/usr/bin/env kotlinc -script -J-Xmx2g

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val files = listOf(
	"14.0.0/clang-c/Index.h" to "clang.14.0.0.ast.json"
)

files
	.filter { (_, output) -> output.let(::File).exists().not() }
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