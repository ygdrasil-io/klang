package klang.jvm

import com.sun.jna.PointerType

class Diagnostic : PointerType() {
    enum class DisplayOptions {
        DISPLAY_SOURCE_LOCATION,
        DISPLAY_COLUMN,
        DISPLAY_SOURCE_RANGES,
        DISPLAY_OPTION,
        DISPLAY_CATEGORY_ID,
        DISPLAY_CATEGORY_NAME
    }

    enum class Severity {
        IGNORED,
        NOTE,
        WARNING,
        ERROR,
        FATAL
    }

    fun format(vararg options: DisplayOptions): String {
        val flags = buildOptionsMask(*options)
        return Clang.formatDiagnostic(this, flags)
    }

    val severity: Severity
        get() {
            val severity = Clang.getDiagnosticSeverity(this)
            return Severity.values()[severity]
        }
}