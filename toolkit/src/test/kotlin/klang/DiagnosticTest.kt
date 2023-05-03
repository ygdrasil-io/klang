package klang

import io.kotest.core.spec.style.StringSpec
import klang.jvm.clang_createIndex
import klang.jvm.Diagnostic
import klang.jvm.Diagnostic.DisplayOptions
import klang.jvm.NativePool
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

private const val EXPECTED_SUFFIX = ".expected"

class DiagnosticTest : StringSpec({

    afterTest { (_, _) ->
        NativePool.disposeAll()
    }

    val files = File(getTestDataDir(), "diagnostic")
        .listFiles()
        ?.filter { it.name.endsWith(EXPECTED_SUFFIX).not() }
        ?: error("No files found")

    files.forEach { file ->
        "test ${file.name}" {

            val index = clang_createIndex(false, false)
            val unit = index.parseTranslationUnit(file.path, arrayOf())
            val diagnostics = unit.diagnostics
            val actual: String = serializeDiagnostics(diagnostics)
            createOrCompare(actual, file.toString() + EXPECTED_SUFFIX)
        }
    }

})

private fun serializeDiagnostics(diagnostics: List<Diagnostic>): String {
    val sw = StringWriter()
    val out = PrintWriter(sw)
    for (diagnostic in diagnostics) {
        var s = diagnostic.format(DisplayOptions.DISPLAY_SOURCE_LOCATION)
        s = s.substring(s.lastIndexOf('/') + 1)
        s = s.substring(s.lastIndexOf('\\') + 1)
        out.println(s)
    }
    out.close()
    return sw.toString()
}