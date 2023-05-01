package klang.jvm

import klang.jvm.binding.CXType

class Type(private val type: CXType.ByValue) {
    val kind: TypeKind
        get() = TypeKind.fromNative(type.kind)
}