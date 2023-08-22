package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.domain.MemoryLayout

class Scoped(
    val kind: Kind,
    var layout: MemoryLayout?,
    val declarations: MutableList<Declaration>,
    name: String,
    pos: Cursor
) : Declaration(name, pos) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? Scoped)?.let {
            kind == it.kind &&
                    declarations == it.declarations
        } ?: false
    }

    /**
     * The scoped declaration kind.
     */
    enum class Kind {

        NAMESPACE,
        CLASS,
        ENUM,
        STRUCT,
        UNION,
        BITFIELDS,
        TOPLEVEL,
        ACCESS_SPECIFIER
    }
}

/**
 * Creates a new struct declaration with given name, layout and member declarations.
 * @param cursor the struct declaration cursor.
 * @param name the struct declaration name.
 * @param layout the struct declaration layout.
 * @param declarations the struct declaration member declarations.
 * @return a new struct declaration with given name, layout and member declarations.
 */
fun struct(
    cursor: Cursor,
    name: String,
    layout: MemoryLayout,
    declarations: MutableList<Declaration>
): Scoped {
    return Scoped(Scoped.Kind.STRUCT, layout, declarations, name, cursor)
}

/**
 * Creates a new bitfields group declaration with given name and layout.
 * @param cursor the bitfields group declaration position.
 * @param layout the bitfields group declaration layout.
 * @param bitfields the bitfields group member declarations.
 * @return a new bitfields group declaration with given name and layout.
 */
fun bitfields(
    cursor: Cursor,
    layout: MemoryLayout,
    bitfields: MutableList<Variable>
): Scoped {
    return Scoped(Scoped.Kind.BITFIELDS, layout, bitfields as MutableList<Declaration>, "", cursor)
}

/**
 * Creates a new union declaration with given name, layout and member declarations.
 * @param pos the union declaration position.
 * @param name the union declaration name.
 * @param layout the union declaration layout.
 * @param decls the union declaration member declarations.
 * @return a new union declaration with given name, layout and member declarations.
 */
fun union(
    cursor: Cursor,
    name: String,
    layout: MemoryLayout,
    declarations: MutableList<Declaration>
): Scoped {
    return Scoped(Scoped.Kind.UNION, layout, declarations, name, cursor)
}

/**
 * Creates a new toplevel declaration with given member declarations.
 * @param cursor the toplevel declaration cursor.
 * @param declarations the toplevel declaration member declarations.
 * @return a new toplevel declaration with given member declarations.
 */
fun toplevel(
    cursor: Cursor,
    declarations: MutableList<Declaration>
): Scoped {
    return Scoped(Scoped.Kind.TOPLEVEL, null, declarations, "<toplevel>", cursor)
}


/**
 * Creates a new namespace declaration, name and member declarations.
 * @param cursor the scoped declaration position.
 * @param name the scoped declaration name.
 * @param declarations children declaration
 * @return a new scoped declaration with given kind, name, layout and member declarations.
 */
fun namespace(
    cursor: Cursor,
    name: String,
    declarations: MutableList<Declaration>
): Scoped {
    return Scoped(Scoped.Kind.NAMESPACE, null, declarations, name, cursor)
}

/**
 * Creates a new class declaration, name and member declarations.
 * @param cursor the scoped declaration position.
 * @param name the scoped declaration name.
 * @param declarations children declaration
 * @return a new scoped declaration with given kind, name, layout and member declarations.
 */

fun Cursor.classDeclaration(
    name: String,
    declarations: MutableList<Declaration> = mutableListOf()
): Scoped {
    return Scoped(Scoped.Kind.CLASS, null, declarations, name, this)
}

fun Cursor.accessSpecifier(
    name: String,
    declarations: MutableList<Declaration>
): Scoped {
    return Scoped(Scoped.Kind.ACCESS_SPECIFIER, null, declarations, name, this)
}
