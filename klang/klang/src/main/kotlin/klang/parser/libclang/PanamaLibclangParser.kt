package klang.parser.libclang

import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.NameableDeclaration
import klang.domain.NativeFunction
import klang.parse
import klang.parser.libclang.panama.toLocalDeclaration
import klang.parser.libclang.panama.toNativeEnumeration
import klang.parser.libclang.panama.toNativeStructure
import klang.parser.libclang.panama.toTypeRef
import mu.KotlinLogging
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.Declaration.Scoped
import org.openjdk.jextract.Declaration.Typedef
import org.openjdk.jextract.JextractTool
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

fun parseFileWithPanama(file: String): DeclarationRepository = InMemoryDeclarationRepository().apply {
	val header = Path.of(file)

	val clangArguments = inferPlatformIncludePath()
		?.let { "-I$it" }
		?.let { arrayOf(it) }
		?: arrayOf()

	val topLevel = parse(
		listOf(header),
		*clangArguments
	)

	assert(topLevel.kind() == Declaration.Scoped.Kind.TOPLEVEL)

	topLevel.members()
		.asSequence()
		.map {
			when (it) {
				is Scoped -> it.toLocalDeclaration()
				is Typedef -> it.toLocalDeclaration()
				is Declaration.Function -> it.toLocalDeclaration()
				else -> {
					logger.error { "not found $it" }
					null
				}
			}
		}
		.filterNotNull()
		.forEach { save(it) }

}


private fun Scoped.toLocalDeclaration(): NameableDeclaration? {
	return when (kind()) {
		Declaration.Scoped.Kind.ENUM -> toNativeEnumeration()
		Declaration.Scoped.Kind.STRUCT -> toNativeStructure()

		else -> {
			logger.error { "not found ${kind()}" }
			null
		}
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