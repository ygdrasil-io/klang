package klang.parser

import klang.jvm.Cursor
import klang.jvm.Type
import klang.parser.domain.MemoryLayout
import klang.parser.domain.declaration.Declaration
import klang.parser.domain.declaration.Scoped
import klang.parser.domain.declaration.Variable
import klang.parser.domain.declaration.struct
import klang.parser.domain.type.Typed.Declared
import klang.parser.domain.type.Typed

/**
 * MemoryLayout computer for C structs.
 */
internal class StructLayoutComputer(
    typeMaker: TypeMaker,
    var offset: Long,
    parent: Type,
    type: Type
): RecordLayoutComputer(typeMaker, parent, type) {

    private var actualSize = 0L

    // List to collect bitfield fields to process later, may be null
    private var bitfieldDecls: MutableList<Declaration>? = null
    private var bitfieldLayouts: MutableList<MemoryLayout>? = null


    override fun addField(declaration: Declaration?) {
        if (bitfieldDecls != null) {
            if (declaration == null) return  //TODO: remove nullability
            bitfieldDecls?.add(declaration)
            when (declaration) {
                is Scoped ->  declaration.layout
                is Variable -> declaration.layout
                else -> null
            }?.let { layout ->
                fieldLayouts.add(if (declaration.name.isEmpty()) layout else layout.copy(name = declaration.name))
            }
        } else {
            super.addField(declaration)
        }
    }


    override fun addPadding(bits: Long) {
        if (bitfieldDecls != null) {
            bitfieldLayouts?.add(MemoryLayout(bits))
        } else {
            super.addPadding(bits)
        }
    }


    override fun startBitfield() {
        /*
         * In a struct, a bitfield field is seen after a non-bitfield.
         * Initialize bitfieldLayouts list to collect this and subsequent
         * bitfield layouts.
         */
        if (bitfieldDecls == null) {
            bitfieldDecls = mutableListOf()
            bitfieldLayouts = mutableListOf()
        }
    }

    override fun processField(c: Cursor) {
        val isBitfield = c.isBitField
        val expectedOffset: Long = offsetOf(parent, c)
        if (expectedOffset > offset) {
            addPadding(expectedOffset - offset)
            actualSize += (expectedOffset - offset)
            offset = expectedOffset
        }
        if (isBitfield) {
            startBitfield()
        } else { // !isBitfield
            /*
             * We may be crossing from bit fields to non-bitfield field.
             *
             * struct Foo {
             *     int i:12;
             *     int j:20;
             *     int k; // <-- processing this
             *     int m;
             * }
             */
            handleBitfields()
        }
        addField(offset, parent, c)
        val size: Long = fieldSize(c)
        offset += size
        actualSize += size
    }


    override fun finishRecord(anonName: String?): Typed.Declared {
        // pad at the end, if any
        val expectedSize = type.size() * 8
        if (actualSize < expectedSize) {
            addPadding(expectedSize - actualSize)
        }

        /*
         * Handle bitfields at the end, if any.
         *
         * struct Foo {
         *     int i,j, k;
         *     int f:10;
         *     int pad:12;
         * }
         */
        handleBitfields()

        val fields = fieldLayouts.toTypedArray()
        val groupLayout = MemoryLayout.structLayout(fields).let {
            when {
                cursor.spelling.isNotEmpty() -> it.copy(name = cursor.spelling)
                anonName != null -> it.copy(name = anonName)
                else -> it
            }
        }
        return Declared(
            cursor.parentName() + cursor.spelling,
            struct(
                cursor, cursor.spelling, groupLayout, fieldDecls.filterNotNull().toMutableList()
            )
        )
    }

    // process bitfields if any and clear bitfield layouts
    private fun handleBitfields() {
        if (bitfieldDecls != null) {
            val prevBitfieldLayouts = bitfieldLayouts ?: listOf()
            val prevBitfieldDecls = bitfieldDecls?.filterIsInstance<Variable>() ?: listOf()
            bitfieldDecls = null
            if (prevBitfieldDecls.isNotEmpty()) {
                addField(
                    bitfield(
                        prevBitfieldLayouts,
                        prevBitfieldDecls.toMutableList()
                    )
                )
            }
        }
    }
}