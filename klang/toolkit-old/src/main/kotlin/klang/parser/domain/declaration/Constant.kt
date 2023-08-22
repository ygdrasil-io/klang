package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.domain.type.Typed

/**
 * Creates a new constant declaration with given name and type.
 * @param typed the constant declaration type.
 * @param value the constant declaration position.
 * @param name the constant declaration name.
 * @param value the constant declaration value.
 * @return a new constant declaration with given name and type.
 */
class Constant(
    val typed: Typed,
    val value: Any,
    name: String,
    cursor: Cursor
) : Declaration(name, cursor) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? Constant)?.let {
            typed == it.typed &&
                    value == it.value
        } ?: false

    }

}