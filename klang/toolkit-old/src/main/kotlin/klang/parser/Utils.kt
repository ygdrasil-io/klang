package klang.parser


import klang.jvm.Cursor
import klang.jvm.CursorKind
import klang.parser.domain.type.Delegated
import klang.parser.domain.type.Typed


fun Typed.isPointerType(): Boolean {
    return when (this) {
        is Delegated -> kind == klang.parser.domain.type.Delegated.Kind.POINTER
        else -> false
    }
}

fun Cursor.flattenableChildren(): List<Cursor> {
    return children()
        .filter { cx -> cx.isAnonymousStruct || cx.kind === CursorKind.FIELD_DECL }
}

//TODO
val IS_WINDOWS = false
    //System.getProperty("os.name").startsWith("Windows")