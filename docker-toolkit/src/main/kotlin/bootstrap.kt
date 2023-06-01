import klang.tools.generateAstFromDocker
import java.io.File

fun main() {
	generateClangAst("14.0.0")

	val files = listOf(
		"typedef-enum.h" to "typedef-enum.h.ast",
		"enum.h" to "enum.h.ast",
		"struct.h" to "struct.h.ast",
		"typedef-struct.h " to "typedef-struct.h.ast",
		"functions.h " to "functions.h.ast",
		"typedef.h " to "typedef.h.ast"
	)

	val sampleSourcePath = File(".")
		.resolve("klang")
		.resolve("sample")
		.resolve("c")

	files.forEach { (source, output) ->
		generateAstFromDocker(
			sampleSourcePath.absolutePath,
			source,
			clangAstOutput = sampleSourcePath.resolve(output)
		)
	}
}

fun generateClangAst(version: String) {
	val clangSourcePath = File(".")
		.resolve("clang")
		.resolve(version)
	val clangQueryAstOutput = clangSourcePath
		.resolve("clang-query.ast")
	val clangAstOutput = clangSourcePath
		.resolve("clang.ast")
	val clangJsonAstOutput = clangSourcePath
		.resolve("clang.ast.json")

	generateAstFromDocker(
		clangSourcePath.absolutePath,
		"clang-c/Index.h",
		clangAstOutput = clangAstOutput,
		clangQueryAstOutput = null,
		clangJsonAstOutput = null
	)
}

