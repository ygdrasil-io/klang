package klang.jvm

import klang.jvm.binding.CXCursor

class Cursor(private val cursor: CXCursor.CXCursorByValue) {
    val spelling: String
        get() = Clang.getCursorSpelling(cursor)
    val kind: CursorKind // We should've called clang_getCursorKind here, but this works and is more efficient
        get() = CursorKind.fromNative(cursor.kind)
    val type: Type
        get() = Type(Clang.getCursorType(cursor))
}