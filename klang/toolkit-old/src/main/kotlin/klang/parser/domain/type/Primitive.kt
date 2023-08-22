package klang.parser.domain.type

import klang.parser.IS_WINDOWS
import klang.parser.UnsupportedLayouts
import klang.parser.domain.MemoryLayout
import klang.parser.domain.ValueLayout

class Primitive(
    val kind: Kind
): Typed() {


    /**
     * The primitive type kind.
     */
    enum class Kind(val typeName: String, val layout: MemoryLayout?) {

        Void("void", null),
        Null("null", null),
        Bool("_Bool", ValueLayout.JAVA_BYTE),
        Char("char", ValueLayout.JAVA_BYTE),
        Char16("char16", UnsupportedLayouts.CHAR16),
        Short("short", ValueLayout.JAVA_SHORT),
        Int("int", ValueLayout.JAVA_INT),
        Long("long", if (IS_WINDOWS) ValueLayout.JAVA_INT else ValueLayout.JAVA_LONG),
        LongLong("long long", ValueLayout.JAVA_LONG),
        Int128("__int128", UnsupportedLayouts.__INT128),
        Float("float", ValueLayout.JAVA_FLOAT),
        Double("double", ValueLayout.JAVA_DOUBLE),
        LongDouble("long double", UnsupportedLayouts.LONG_DOUBLE),
        Float128("float128", UnsupportedLayouts._FLOAT128),
        HalfFloat("__fp16", UnsupportedLayouts.__FP16),
        WChar("wchar_t", UnsupportedLayouts.WCHAR_T);
    }
}

fun void(): Primitive {
    return Primitive(Primitive.Kind.Void)
}

fun nullPointer(): Primitive {
    return Primitive(Primitive.Kind.Null)
}


/**
 * Creates a new primitive type given kind.
 * @param kind the primitive type kind.
 * @return a new primitive type with given kind.
 */
fun primitive(kind: Primitive.Kind): Primitive {
    return Primitive(kind)
}