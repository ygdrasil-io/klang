package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.domain.type.Typed

class Typedef(
    val typed: Typed,
    name: String,
    cursor: Cursor,
) : Declaration(name, cursor) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Typedef &&
                name == other.name &&
                typed == other.typed
    }
}
