import klang.tools.generateAstFromDocker
import java.io.File
import java.util.*


private val OS = System.getProperty("os.name").lowercase(Locale.getDefault())

fun main() {
	//generateCppSampleAst()
	//generateClangAst("14.0.0")
	generateCSampleAst()
	if (isMac()) {
		//generateObjectiveCSampleAst()
	}
}

fun generateObjectiveCSampleAst() {
	val files = listOf(
		"class.h" to "class.h.ast",
	)

	val sampleSourcePath = File(".")
		.resolve("klang")
		.resolve("sample")
		.resolve("objective-c")

	files.forEach { (source, output) ->
		generateAstFromDocker(
			sampleSourcePath.absolutePath,
			source,
			isObjectiveC = true,
			clangAstOutput = sampleSourcePath.resolve(output)
		)
	}
}

private fun generateCSampleAst() {
	val files = listOf(
		"typedef-enum.h" to "typedef-enum.h.ast",
		"enum.h" to "enum.h.ast",
		"struct.h" to "struct.h.ast",
		"typedef-struct.h " to "typedef-struct.h.ast",
		"functions.h " to "functions.h.ast",
		"typedef.h " to "typedef.h.ast",
		"exhaustive-typedef.h " to "exhaustive-typedef.h.ast",
	)

	val sampleSourcePath = File(".")
		.resolve("klang")
		.resolve("src")
		.resolve("test")
		.resolve("c")

	files.forEach { (source, output) ->
		generateAstFromDocker(
			sampleSourcePath.absolutePath,
			source,
			clangAstOutput = sampleSourcePath.resolve(output),
			clangJsonAstOutput = sampleSourcePath.resolve("$output.json")
		)
	}
}


private fun generateCppSampleAst() {
	val files = listOf(
		"class.hpp" to "class.hpp.ast"
	)

	val sampleSourcePath = File(".")
		.resolve("klang")
		.resolve("src")
		.resolve("test")
		.resolve("cpp")

	files.forEach { (source, output) ->
		generateAstFromDocker(
			sampleSourcePath.absolutePath,
			source,
			clangAstOutput = sampleSourcePath.resolve(output),
			clangJsonAstOutput = sampleSourcePath.resolve("$output.json")
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

private fun isMac(): Boolean {
	return OS.indexOf("mac") >= 0
}
