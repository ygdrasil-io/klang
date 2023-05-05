package klang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import klang.jvm.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class BasicTest : StringSpec({

    afterTest { (_, _) ->
        NativePool.disposeAll()
    }

    "test Version" {
        Clang.getClangVersion() shouldStartWith  "clang version 14.0.0"
    }

    "test CreateIndex" {
        val booleans = booleanArrayOf(false, true)
        for (excludeDeclarationsFromPCH in booleans) {
            for (displayDiagnostics in booleans) {
                clang_createIndex(excludeDeclarationsFromPCH, displayDiagnostics) shouldNotBe null
            }
        }
    }

    "test ParseTranslationUnit" {
        createIndex(excludeDeclarationsFromPCH = false, false).use { index ->
            index.parseTranslationUnit(createTempFileWithContents("").absolutePath, arrayOf())
        }
    }

    "test TranslationException" {
        val index = createIndex(excludeDeclarationsFromPCH = false, false)
        try {
            index.parseTranslationUnit(null, arrayOf())
            error("TranslationException expected")
        } catch (_: TranslationException) {

        }
    }

    "test DiagnosticDisplayOptions" {
        val index = createIndex(excludeDeclarationsFromPCH = false, false)
        val unit = index.parseTranslationUnit(getDir() + "diagnosticDisplayOptions.h", arrayOf())
        val diagnostics = unit.diagnostics
        diagnostics.size shouldBe 1
        val diagnostic = diagnostics.iterator().next()
        val sw = StringWriter()
        val out = PrintWriter(sw)
        out.println(diagnostic.format(Diagnostic.DisplayOptions.DISPLAY_SOURCE_LOCATION))
        out.println(diagnostic.format(
            Diagnostic.DisplayOptions.DISPLAY_SOURCE_LOCATION,
            Diagnostic.DisplayOptions.DISPLAY_COLUMN
        ))
        out.println(diagnostic.format(
            Diagnostic.DisplayOptions.DISPLAY_SOURCE_LOCATION,
            Diagnostic.DisplayOptions.DISPLAY_SOURCE_RANGES
        ))
        out.println(diagnostic.format(Diagnostic.DisplayOptions.DISPLAY_OPTION))
        out.println(diagnostic.format(Diagnostic.DisplayOptions.DISPLAY_CATEGORY_ID))
        out.println(diagnostic.format(Diagnostic.DisplayOptions.DISPLAY_CATEGORY_NAME))
        out.close()
        createOrCompare(sw.toString(), getDir() + "diagnosticDisplayOptions.txt")
    }

    "test DiagnosticSeverity" {
        val index = createIndex(excludeDeclarationsFromPCH = false, false)
        val unit = index.parseTranslationUnit(getDir() + "diagnosticSeverity.h", arrayOf())
        val diagnostics = unit.diagnostics
        val actual: MutableList<Diagnostic.Severity> = ArrayList(diagnostics.size)
        for (diagnostic in diagnostics) {
            actual.add(diagnostic.severity)
        }
        actual shouldBe listOf(
            Diagnostic.Severity.ERROR,
            Diagnostic.Severity.WARNING,
            Diagnostic.Severity.FATAL
        )
    }

    "test CursorKinds" {
        // For some reason, CXCursorKind spelling is not always equal to the enum constant name, so we check spellings with a saved list
        val sw = StringWriter()
        val out = PrintWriter(sw)
        for (kind in CursorKind.values()) {
            out.print(kind.toNative())
            val spelling = kind.spelling
            if (lettersOnly(spelling) != lettersOnly(kind.toString())) {
                out.print(" $kind")
            }
            out.println(" $spelling")
        }
        out.close()
        createOrCompare(sw.toString(), getDir() + "cursorKinds.txt")
    }

    "test TypeKinds" {
        val swNative = StringWriter()
        val swEnum = StringWriter()
        val outNative = PrintWriter(swNative)
        val outEnum = PrintWriter(swEnum)
        for (kind in TypeKind.values()) {
            outNative.println(lettersOnly(kind.spelling))
            outEnum.println(lettersOnly(kind.toString()))
        }
        outNative.close()
        outEnum.close()
        swEnum.toString() shouldBe swNative.toString()
    }
})

private fun getDir(): String {
    return getTestDataDir().toString() + "/basic/"
}

private fun lettersOnly(s: String): String {
    return s.replace("[^A-Za-z]+".toRegex(), "").lowercase(Locale.getDefault())
}