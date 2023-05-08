

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXString;

public class Clang {
    public static final Clang INSTANCE = new Clang();

    private Clang() {}

    @NotNull
    public Index createIndex(boolean excludeDeclarationsFromPCH, boolean displayDiagnostics) {
        Index index = LibClang.I.createIndex(excludeDeclarationsFromPCH, displayDiagnostics);
        return NativePool.I.record(index);
    }

    @NotNull
    public String getVersion() {
        CXString.ByValue version = LibClang.I.getClangVersion();
        NativePool.I.record(version);
        return LibClang.I.getCString(version);
    }

    public void disposeAll() {
        NativePool.I.disposeAll();
    }
}
