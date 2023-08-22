package klang.parser.domain.declaration

import klang.jvm.Cursor
import klang.parser.PrettyPrinter
import klang.parser.domain.MemoryLayout

abstract class Declaration(
    val name: String,
    val cursor: Cursor,
    val attributes: MutableMap<String, List<String>> = mutableMapOf()
) {

    override fun toString(): String {
        return PrettyPrinter().print(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return other is Declaration && name == other.name
    }

    companion object {

        fun scoped(
            kind: Scoped.Kind,
            cursor: Cursor,
            name: String,
            declarations: List<Declaration>
        ): Scoped {
            return Scoped(kind, null, declarations.toMutableList(), name, cursor)
        }

        fun scoped(
            kind: Scoped.Kind,
            cursor: Cursor,
            name: String,
            layout: MemoryLayout,
            declarations: List<Declaration>
        ): Scoped {
            return Scoped(kind, layout, declarations.toMutableList(), name, cursor)
        }

        fun enumeration(cursor: Cursor, name: String, declarations: List<Declaration>): Scoped {
            return Scoped(Scoped.Kind.ENUM, null, declarations.toMutableList(), name, cursor);
        }

        fun enumeration(cursor: Cursor, name: String, layout: MemoryLayout, declarations: List<Declaration>): Scoped {
            return Scoped(Scoped.Kind.ENUM, layout, declarations.toMutableList(), name, cursor);
        }


    }
}

