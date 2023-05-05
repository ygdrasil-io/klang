package klang.jvm

import com.sun.jna.ptr.IntByReference
import klang.jvm.binding.CXTokenByReference
import klang.jvm.binding.CXTranslationUnit
import klang.jvm.binding.CXUnsavedFile

private const val MAX_RETRIES = 10

class TranslationUnit(internal val pointer: CXTranslationUnit) {

    enum class Flag {
        DETAILED_PREPROCESSING_RECORD,
        INCOMPLETE,
        PRECOMPILED_PREAMBLE,
        CACHE_COMPLETION_RESULTS,
        FOR_SERIALIZATION,
        @Deprecated("")
        CXX_CHAINED_PCH,
        SKIP_FUNCTION_BODIES,
        INCLUDE_BRIEF_COMMENTS_IN_CODE_COMPLETION
    }

    val cursor
        get() = Cursor(Clang.getTranslationUnitCursor(pointer))

    val diagnostics: List<Diagnostic>
        get() {
            return (0 until Clang.getNumDiagnostics(pointer)).map {
                Clang.getDiagnostic(pointer, it)
                    .apply { NativePool.record(this) }
            }
        }


    fun save(path: String) {
        Clang.saveTranslationUnit(pointer, path, 0).toUInt()
            .let(SaveError::of)
            .takeIf { it != SaveError.None }
            ?.let { error("Failed to save translation unit to $path with error $it") }
    }

    fun processDiagnostics(diagnosticHandlers: (Diagnostic) -> Unit) {
        diagnostics.forEach(diagnosticHandlers)
    }

    fun reparse(dh: (Diagnostic) -> Unit, inMemoryFiles: List<Index.UnsavedFile>) {
        reparse(inMemoryFiles)
        processDiagnostics(dh)
    }

    fun tokens(range: SourceRange): List<String> {
        val tokens = tokenize(range)
        return (0 until tokens.size).map { i -> tokens.getToken(i).spelling(this) }
    }

    private fun reparse(inMemoryFiles: List<Index.UnsavedFile>) {
        val files = inMemoryFiles.map {
            CXUnsavedFile().apply {
                filename = it.file
                contents = it.contents
                length = it.contents.length
            }
        }.toTypedArray()
            .takeIf { it.isNotEmpty() }

        var code: ErrorCode
        var tries = 0
        do {
            code =
                Clang.reparseTranslationUnit(
                    pointer,
                    inMemoryFiles.size,
                    files,
                    Clang.defaultReparseOptions(pointer)
                )
                    .let { ErrorCode.of(it.toUInt()) }

        } while (code === ErrorCode.Crashed && ++tries < MAX_RETRIES) // this call can crash on Windows. Retry in that case.
        check(code !== ErrorCode.Success) { "Re-parsing failed: $code" }
    }

    fun tokenize(range: SourceRange): Tokens {
        val tokens = CXTokenByReference()
        val numTokens = IntByReference()
        Clang.tokenize(pointer, range.range, tokens.pointer, numTokens.pointer)
        return Tokens(tokens, numTokens.value)
    }
}