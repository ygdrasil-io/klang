package klang.parser

import klang.jvm.Cursor
import klang.jvm.Type
import klang.jvm.TypeKind
import klang.parser.domain.MemoryLayout
import klang.parser.domain.declaration.Declaration
import klang.parser.domain.declaration.Variable
import klang.parser.domain.declaration.union
import klang.parser.domain.type.Typed
import kotlin.math.max

/**
 * MemoryLayout computer for C unions.
 */
internal class UnionLayoutComputer(
    typeMaker: TypeMaker,
    private val offset: Long,
    parent: Type,
    type: Type
) : RecordLayoutComputer(typeMaker, parent, type) {

    private var actualSize = 0L

    override fun processField(c: Cursor) {
        val expectedOffset = offsetOf(parent, c)
        check(expectedOffset <= offset) { "No padding in union elements!" }
        addField(offset, parent, c)
        actualSize = max(actualSize, fieldSize(c))
    }

    override fun startBitfield() {
        // do nothing
    }

    override fun field(c: Cursor): Declaration? {
        return if (c.isBitField) {
            val variable = super.field(c) as Variable
            bitfield(listOf(variable.layout), mutableListOf(variable))
        } else {
            super.field(c)
        }
    }


    override fun fieldSize(c: Cursor): Long {
        return if (c.type.kind == TypeKind.IncompleteArray) {
            0
        } else if (c.isBitField) {
            c.getBitFieldWidth().toLong()
        } else {
            c.type.size() * 8
        }
    }

    override fun finishRecord(anonName: String?): Typed.Declared {
        // size mismatch indicates use of bitfields in union
        val expectedSize = type.size() * 8
        if (actualSize < expectedSize) {
            // emit an extra padding of expected size to make sure union layout size is computed correctly
            addPadding(expectedSize)
        } else if (actualSize > expectedSize) {
            throw AssertionError("Invalid union size - expected: $expectedSize; found: $actualSize")
        }
        val fields = fieldLayouts.toTypedArray()
        var g = MemoryLayout.unionLayout(fields)
        if (!cursor.spelling.isEmpty()) {
            g = g.copy(name = cursor.spelling)
        } else if (anonName != null) {
            g = g.copy(name = anonName)
        }
        return Typed.Declared(
            cursor.fullName,
            union(
                cursor, cursor.spelling, g, fieldDecls.filterNotNull().toMutableList()
            )
        )
    }
}