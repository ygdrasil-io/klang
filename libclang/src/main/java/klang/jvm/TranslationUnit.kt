package klang.jvm

import com.sun.jna.PointerType

class TranslationUnit : PointerType() {
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

    val diagnostics: List<Diagnostic>
        get() {
            val n = Clang.getNumDiagnostics(this)
            val result: MutableList<Diagnostic> = ArrayList(n)
            for (i in 0 until n) {
                val diagnostic = Clang.getDiagnostic(this, i)
                result.add(NativePool.record(diagnostic))
            }
            return result
        }
}