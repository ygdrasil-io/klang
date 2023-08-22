package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.domain.MemoryLayout
import klang.parser.domain.type.Typed

class Variable(
    val kind: Kind,
    val typed: Typed,
    val layout: MemoryLayout?,
    name: String,
    cursor: Cursor
) : Declaration(name, cursor) {

    override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return (other as? Variable)?.let {
                kind == it.kind &&
                        typed == it.typed
            } ?: false
    }

    enum class Kind {
        GLOBAL,
        FIELD,
        BITFIELD,
        PARAMETER
    }
}


/**
 * Creates a new bitfield declaration with given name, type and layout.
 * @param name the bitfield declaration name.
 * @param type the bitfield declaration type.
 * @param layout the bitfield declaration layout.
 * @return a new bitfield declaration with given name, type and layout.
 */
fun Cursor.bitfield(name: String, type: Typed, layout: MemoryLayout): Variable {
    return Variable(
        Variable.Kind.BITFIELD,
        type,
        layout,
        name,
        this
    )
}

/**
 * Creates a new field declaration with given name and type.
 * @param cursor the field declaration position.
 * @param name the field declaration name.
 * @param type the field declaration type.
 * @return a new field declaration with given name and type.
 */
fun field(cursor: Cursor, name: String, type: Typed): Variable? {
    return null
}


/**
 * Creates a new variable declaration with given kind, name and type.
 * @param kind the variable declaration kind.
 * @param pos the variable declaration position.
 * @param name the variable declaration name.
 * @param typed the variable declaration type.
 * @return a new variable declaration with given kind, name and type.
 */
fun variable(
    kind: Variable.Kind,
    cursor: Cursor,
    name: String,
    typed: Typed
): Variable {
    return Variable(kind, typed, null, name, cursor)
}