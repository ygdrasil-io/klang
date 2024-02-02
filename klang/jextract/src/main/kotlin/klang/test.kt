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
