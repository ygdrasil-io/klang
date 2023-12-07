package klang

import org.openjdk.jextract.Declaration
import org.openjdk.jextract.JextractTool
import org.openjdk.jextract.impl.CodeGenerator
import org.openjdk.jextract.impl.IncludeHelper
import org.openjdk.jextract.impl.Parser
import org.openjdk.jextract.impl.Writer
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.tools.JavaFileObject
import kotlin.io.path.exists

const val headerPath = "/Users/chaos/Workspace/klang2/bindings/sdl/libsdl/build/klang/e3832363bd9226782f9d2db764c947d8/"

/**
 * Usage: jextract <options> <header file>
 *
 * Option                             Description
 * ------                             -----------
 * -?, -h, --help                     print help
 * -D --define-macro <macro>=<value>  define <macro> to <value> (or 1 if <value> omitted)
 * -I, --include-dir <dir>            add directory to the end of the list of include search paths
 * --dump-includes <file>             dump included symbols into specified file
 * --header-class-name <name>         name of the generated header class. If this option is not
 *                                    specified, then header class name is derived from the header
 *                                    file name. For example, class "foo_h" for header "foo.h".
 * --include-function <name>          name of function to include
 * --include-constant <name>          name of macro or enum constant to include
 * --include-struct <name>            name of struct definition to include
 * --include-typedef <name>           name of type definition to include
 * --include-union <name>             name of union definition to include
 * --include-var <name>               name of global variable to include
 * -l, --library <name | path>        specify a library by platform-independent name (e.g. "GL")
 *                                    or by absolute path ("/usr/lib/libGL.so") that will be
 *                                    loaded by the generated class.
 * --output <path>                    specify the directory to place generated files. If this
 *                                    option is not specified, then current directory is used.
 * --source                           generate java sources
 * -t, --target-package <package>     target package name for the generated classes. If this option
 *                                    is not specified, then unnamed package is used.
 * --version                          print version information and exit
 *
 */

fun main() {

	val header = Path.of("${headerPath}SDL2/SDL.h")
	val targetPackage = "libsdl2"
	val libraryNames = listOf("SDL2")
	val headerName = header.fileName.toString()
	val includeHelper = IncludeHelper()
	val output = Path.of("./binding")

	output.toFile()
		.takeIf { it.exists() && it.isDirectory }
		?.deleteRecursively()

    val clangArguments = inferPlatformIncludePath()
        ?.let { "-I$it" }
        ?.let { arrayOf(it) }
        ?: arrayOf()

    val topLevel = parse(
        listOf(header),
        *clangArguments
    )

	val files = generateInternal(
		topLevel, headerName,
		targetPackage, includeHelper, libraryNames
	)

	JextractTool.write(output, false, files)

//	val args = arrayOf(
//		"${headerPath}SDL2/SDL.h",
//		"--output",
//		"./binding",
//		"-t",
//		"libsdl2",
//		"-lSDL2"
//	)
//
//	val m = JextractTool(PrintWriter(System.out, true), PrintWriter(System.err, true))
//	exitProcess(m.run(args))
}

fun parse(headers: List<Path>, vararg parserOptions: String?): Declaration.Scoped {
	val source = if (headers.size > 1) generateTmpSource(headers) else headers.iterator().next()!!
	return Parser().parse(source, Stream.of(*parserOptions).collect(Collectors.toList()))
}

private fun generateTmpSource(headers: List<Path>): Path {
	assert(headers.size > 1)
	try {
		val tmpFile = Files.createTempFile("jextract", ".h")
		tmpFile.toFile().deleteOnExit()
		Files.write(
			tmpFile, headers.stream().map
			{ src: Path -> "#include \"$src\"" }.collect
				(Collectors.toList())
		)
		return tmpFile
	} catch (ioExp: IOException) {
		throw UncheckedIOException(ioExp)
	}
}

private fun inferPlatformIncludePath(): Path? {
	val os = System.getProperty("os.name")
	if (os == "Mac OS X") {
		try {
			val pb: ProcessBuilder = ProcessBuilder().command("/usr/bin/xcrun", "--show-sdk-path")
			val proc = pb.start()
			val str = String(proc.inputStream.readAllBytes())
			val dir = Paths.get(str.trim { it <= ' ' }, "usr", "include")
			if (Files.isDirectory(dir)) {
				return dir
			}
		} catch (ioExp: IOException) {
			if (JextractTool.DEBUG) {
				ioExp.printStackTrace(System.err)
			}
		}
	}
	return null
}

private fun generateInternal(
	decl: Declaration.Scoped, headerName: String,
	targetPkg: String, includeHelper: IncludeHelper, libNames: List<String>
): List<JavaFileObject> {
	return listOf(*CodeGenerator.generate(decl, headerName, targetPkg, includeHelper, libNames))
}

/**
 * Write resulting [JavaFileObject] instances into specified destination path.
 * @param dest the destination path.
 * @param compileSources whether to compile .java sources or not
 * @param files the [JavaFileObject] instances to be written.
 */
@Throws(UncheckedIOException::class)
fun write(dest: Path?, compileSources: Boolean, files: List<JavaFileObject?>?) {
	try {
		Writer(dest, files).writeAll(compileSources)
	} catch (ex: IOException) {
		throw UncheckedIOException(ex)
	}
}