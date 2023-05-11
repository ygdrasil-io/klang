package klang.jvm

import java.util.*

enum class TypeKind {
    Invalid,
    Unexposed,
    Void,
    Bool,
    CharU,
    UChar,
    Char16,
    Char32,
    UShort,
    UInt,
    ULong,
    ULongLong,
    UInt128,
    CharS,
    SChar,
    WChar,
    Short,
    Int,
    Long,
    LongLong,
    Int128,
    Float,
    Double,
    LongDouble,
    NullPtr,
    OVERLOAD,
    DEPENDENT,
    OBJC_ID,
    OBJC_CLASS,
    OBJC_SEL,
    Complex,
    Pointer,
    BlockPointer,
    LVALUE_REFERENCE,
    RVALUE_REFERENCE,
    Record,
    Enum,
    Typedef,
    OBJC_INTERFACE,
    OBJC_OBJECT_POINTER,
    FUNCTION_NO_PROTO,
    FUNCTION_PROTO,
    CONSTANT_ARRAY,
    Vector,
    IncompleteArray,
    Elaborated,
    FunctionProto,
    FunctionNoProto,
    Half,
    Auto,;

    /* package */
    fun toNative(): kotlin.Int {
        return TO_NATIVE[this]
            ?: throw IllegalStateException("No corresponding CXTypeKind value: $this. Probably an incompatible libclang version")
    }

    val spelling: String
        get() = Clang.getTypeKindSpelling(toNative())

    companion object {
        private val FROM_NATIVE: MutableMap<kotlin.Int, TypeKind> = HashMap()
        private val TO_NATIVE: MutableMap<TypeKind, kotlin.Int> = EnumMap(
            TypeKind::class.java
        )

        init {
            // The code below depends on the actual CXCursorKind enum values
            var nativeValue = 0
            for (enumValue in values()) {
                if (enumValue == Complex) {
                    nativeValue = 100
                }
                FROM_NATIVE[nativeValue] = enumValue
                TO_NATIVE[enumValue] = nativeValue
                nativeValue++
            }
        }

        @JvmStatic
        fun fromNative(kind: kotlin.Int): TypeKind {
            return FROM_NATIVE[kind]
                ?: throw IllegalStateException("Unknown CXTypeKind value: $kind. Probably an incompatible libclang version")
        }
    }
}