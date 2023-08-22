package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.domain.type.Lambda

class Function(
    val type: Lambda,
    val parameters: List<Variable>,
    name: String,
    cursor: Cursor
) : Declaration(name, cursor) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;

        return (other as? Function)?.let {
            type == it.type
        } ?: false
    }

}
