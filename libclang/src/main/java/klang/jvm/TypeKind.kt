package klang.jvm

import java.util.*

enum class TypeKind {
    INVALID,
    UNEXPOSED,
    VOID,
    BOOL,
    CHAR_U,
    UCHAR,
    CHAR16,
    CHAR32,
    USHORT,
    UINT,
    ULONG,
    ULONG_LONG,
    UINT128,
    CHAR_S,
    SCHAR,
    WCHAR,
    SHORT,
    INT,
    LONG,
    LONG_LONG,
    INT128,
    FLOAT,
    DOUBLE,
    LONG_DOUBLE,
    NULL_PTR,
    OVERLOAD,
    DEPENDENT,
    OBJC_ID,
    OBJC_CLASS,
    OBJC_SEL,
    COMPLEX,
    POINTER,
    BLOCK_POINTER,
    LVALUE_REFERENCE,
    RVALUE_REFERENCE,
    RECORD,
    ENUM,
    TYPEDEF,
    OBJC_INTERFACE,
    OBJC_OBJECT_POINTER,
    FUNCTION_NO_PROTO,
    FUNCTION_PROTO,
    CONSTANT_ARRAY,
    VECTOR;

    /* package */
    fun toNative(): Int {
        return TO_NATIVE[this]
            ?: throw IllegalStateException("No corresponding CXTypeKind value: $this. Probably an incompatible libclang version")
    }

    val spelling: String
        get() = Clang.getTypeKindSpelling(toNative())

    companion object {
        private val FROM_NATIVE: MutableMap<Int, TypeKind> = HashMap()
        private val TO_NATIVE: MutableMap<TypeKind, Int> = EnumMap(
            TypeKind::class.java
        )

        init {
            // The code below depends on the actual CXCursorKind enum values
            var nativeValue = 0
            for (enumValue in values()) {
                if (enumValue == COMPLEX) {
                    nativeValue = 100
                }
                FROM_NATIVE[nativeValue] = enumValue
                TO_NATIVE[enumValue] = nativeValue
                nativeValue++
            }
        }

        @JvmStatic
        fun fromNative(kind: Int): TypeKind {
            return FROM_NATIVE[kind]
                ?: throw IllegalStateException("Unknown CXTypeKind value: $kind. Probably an incompatible libclang version")
        }
    }
}