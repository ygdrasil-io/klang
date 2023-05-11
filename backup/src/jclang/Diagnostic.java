
package jclang;

import com.sun.jna.PointerType;
import org.jetbrains.annotations.NotNull;
import jclang.structs.CXString;
import jclang.util.Util;

public class Diagnostic extends PointerType {
    public enum DisplayOptions {
        DISPLAY_SOURCE_LOCATION,
        DISPLAY_COLUMN,
        DISPLAY_SOURCE_RANGES,
        DISPLAY_OPTION,
        DISPLAY_CATEGORY_ID,
        DISPLAY_CATEGORY_NAME
    }

    public enum Severity {
        IGNORED,
        NOTE,
        WARNING,
        ERROR,
        FATAL
    }

    @NotNull
    public String format(@NotNull DisplayOptions... options) {
        int flags = Util.buildOptionsMask(options);
        CXString.ByValue string = LibClang.I.formatDiagnostic(this, flags);
        NativePool.I.record(string);
        return LibClang.I.getCString(string);
    }

    @NotNull
    public Severity getSeverity() {
        int severity = LibClang.I.getDiagnosticSeverity(this);
        return Severity.values()[severity];
    }
}
