package klang.jvm

import klang.jvm.binding.CXType

class Type(private val type: CXType.ByValue) {

    val pointeeType: Type
        get() = Type(Clang.getPointeeType(type))

    val resultType: Type
        get() = Type(Clang.getResultType(type))

    val numberOfArgs: Int
        get() = Clang.getNumArgTypes(type)

    val elementType: Type
        get() = Type(Clang.getElementType(type))

    val numberOfElements: Long
        get() = Clang.getNumElements(type)

    val spelling: String
        get() = Clang.getTypeSpelling(type)

    val isVariadic: Boolean
        get() = Clang.isFunctionTypeVariadic(type) != 0

    val declarationCursor
        get() = Cursor(Clang.getTypeDeclaration(type))

    val kind: TypeKind
        get() = TypeKind.fromNative(type.kind)

    val canonicalType: Type
        get() = Type(Clang.getCanonicalType(type))

    fun size(): Long =
        nativeSize()
            .also { if (it < 0) error("fail to get size with code $this") }

    fun getOffsetOf(fieldName: String): Long =
        Clang.Type_getOffsetOf(type, fieldName)
            .also { if (it < 0) error("fail to get offest of $fieldName with error $it") }

    fun equalType(other: Type): Boolean =
        Clang.equalTypes(type, other.type) != 0

    fun argType(idx: Int): Type     =
        Type(Clang.getArgType(type, idx))

    private fun nativeSize(): Long = Clang.Type_getSizeOf(type)
}