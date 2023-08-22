package klang.tools

import java.io.File

class ClangCommandBuilder(
	private val buildPath: File,
	private val sourcePath: File,
	private val clangCommand: String = "clang-16",
) {

	fun build() = listOf(
		clangCommand,
		//"-p", buildPath.absolutePath,
		sourcePath.absolutePath,
		"--use-color=false",
		"-c", "'set output detailed-ast'",
		"-c", "'set traversal IgnoreUnlessSpelledInSource'",
		"-c", "'match decl()'"
	)

}