package klang.tools

import java.io.File

class ClangQueryCommandBuilder(
	private val buildPath: File,
	private val sourcePath: File,
	private val clangQueryCommand: String = "clang-query-16",
) {

	fun build() = listOf(
		clangQueryCommand,
		//"-p", buildPath.absolutePath,
		sourcePath.absolutePath,
		"--use-color=false",
		"-c", "'set output detailed-ast'",
		"-c", "'set traversal IgnoreUnlessSpelledInSource'",
		"-c", "'match decl()'"
	)

}