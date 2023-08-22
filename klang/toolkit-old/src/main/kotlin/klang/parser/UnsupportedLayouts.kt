
package klang.parser

import klang.parser.domain.MemoryLayout
import klang.parser.domain.declaration.Variable
import klang.parser.domain.type.Array
import klang.parser.domain.type.Delegated
import klang.parser.domain.type.Lambda
import klang.parser.domain.type.Primitive
import klang.parser.domain.type.Typed

/*
 * Layouts for the primitive types not supported by ABI implementations.
 */
object UnsupportedLayouts {
    val __INT128 = makeUnsupportedLayout(128, "__int128")
    val LONG_DOUBLE = makeUnsupportedLayout(128, "long double")
    val _FLOAT128 = makeUnsupportedLayout(128, "_float128")
    val __FP16 = makeUnsupportedLayout(16, "__fp16")
    val CHAR16 = makeUnsupportedLayout(16, "char16")
    val WCHAR_T = makeUnsupportedLayout(16, "wchar_t")

    fun firstUnsupportedType(type: Typed): String? {
        return type.accept()
    }

    private fun Typed.accept(): String? {
        return when (this) {
            is Primitive -> visitPrimitive(this)
            is Lambda -> visitFunction(this)
            is Typed.Declared -> visitDeclared(this)
            is Delegated -> visitDelegated(this)
            is Array -> visitArray(this)
            else -> null
        }
    }

    private fun makeUnsupportedLayout(size: Long, name: String): MemoryLayout {
        return MemoryLayout(paddingLayout = size, withBitAlignment = size, name = name)
    }

    private fun visitPrimitive(t: Primitive): String? {
        val layout: MemoryLayout = t.kind.layout ?: MemoryLayout(64)
        return if (layout == __INT128 || layout == LONG_DOUBLE || layout == _FLOAT128 || layout == __FP16) {
            layout.name
        } else {
            null
        }
    }

    private fun visitFunction(t: Lambda): String? {
        for (arg in t.argumentTypeds) {
            return firstUnsupportedType(arg)
        }
        return firstUnsupportedType(t.returnTyped)
    }

    private fun visitDeclared(t: Typed.Declared): String? {
        for (d in t.reference.declarations) {
            if (d is Variable) {
                val unsupported = firstUnsupportedType(d.typed)
                if (unsupported != null) {
                    return unsupported
                }
            }
        }
        return null
    }

    private fun visitDelegated(t: Delegated): String? {
        return if (t.kind !== Delegated.Kind.POINTER) firstUnsupportedType(t.type) else null
        //in principle we should always do this:
        // return firstUnsupportedType(t.type());
        // but if we do that, we might end up with infinite recursion (because of pointer types).
        // Unsupported pointer types (e.g. *long double) are not detected, but they are not problematic layout-wise
        // (e.g. they are always 32- or 64-bits, depending on the platform).
    }

    private fun visitArray(t: Array): String? {
        return firstUnsupportedType(t.elementTyped)
    }


}
