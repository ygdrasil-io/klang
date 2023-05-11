

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXCursor;
import jclang.structs.CXString;

public class Cursor {
    private final CXCursor.ByValue cursor;

    public Cursor(@NotNull CXCursor.ByValue cursor) {
        this.cursor = cursor;
    }

    @NotNull
    public String getSpelling() {
        CXString.ByValue spelling = LibClang.I.getCursorSpelling(cursor);
        NativePool.I.record(spelling);
        return LibClang.I.getCString(spelling);
    }

    @NotNull
    public CursorKind getKind() {
        // We should've called clang_getCursorKind here, but this works and is more efficient
        return CursorKind.fromNative(cursor.kind);
    }

    @NotNull
    public Type getType() {
        return new Type(LibClang.I.getCursorType(cursor));
    }
}
