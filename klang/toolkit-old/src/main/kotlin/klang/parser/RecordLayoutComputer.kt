package klang.parser

import klang.jvm.Cursor
import klang.jvm.CursorKind
import klang.jvm.Type
import klang.jvm.TypeKind
import klang.parser.domain.MemoryLayout
import klang.parser.domain.declaration.*
import klang.parser.domain.declaration.Declaration
import klang.parser.domain.declaration.bitfields
import klang.parser.domain.type.Typed

internal abstract class RecordLayoutComputer(
    val typeMaker: TypeMaker,
    // enclosing struct type (or this struct type for top level structs)
    val parent: Type,
    // this struct type
    val type: Type
) {

    val fieldDecls = mutableListOf<Declaration?>()
    val fieldLayouts = mutableListOf<MemoryLayout>()
    // cursor of this struct
    val cursor = type.declarationCursor.getDefinition()
    private var anonCount = 0

    fun compute(anonName: String): Typed.Declared {
        val fieldCursors = cursor.flattenableChildren()
        for (fc in fieldCursors.toList()) {
            /*
             * Ignore bitfields of zero width.
             *
             * struct Foo {
             *     int i:0
             * }
             *
             * And bitfields without a name.
             * (padding is computed automatically)
             */
            if (fc.isBitField && (fc.getBitFieldWidth() == 0 || fc.spelling.isEmpty())) {
                startBitfield()
                continue
            }
            processField(fc)
        }
        return finishRecord(anonName)
    }

    abstract fun startBitfield()
    abstract fun processField(c: Cursor)
    abstract fun finishRecord(anonName: String?): Typed.Declared

    open fun addField(declaration: Declaration?) {
        fieldDecls.add(declaration)
        if (declaration == null) return //TODO: remove nullability
        when (declaration) {
            is Scoped -> declaration.layout
            is Variable -> declaration.layout
            else -> null
        }?.let { layout ->
            fieldLayouts.add(if (declaration.name.isEmpty()) layout else layout.copy(name = declaration.name))
        }
    }

    open fun addPadding(bits: Long) {
        fieldLayouts.add(MemoryLayout(bits))
    }

    open fun addField(offset: Long, parent: Type, c: Cursor) {
        if (c.isAnonymousStruct) {
            addField(
                (computeAnonymous(
                    typeMaker,
                    offset,
                    parent,
                    c.type,
                    nextAnonymousName()
                ) as Typed.Declared).reference
            )
        } else {
            addField(field(c))
        }
    }
    
    private fun nextAnonymousName(): String {
        return "\$anon$" + anonCount++
    }
    
    open fun field(cursor: Cursor): Declaration? = with(cursor){
        val type = typeMaker.makeType(cursor.type)
        val name = cursor.spelling
        if (cursor.isBitField) {
            val sublayout = MemoryLayout(cursor.getBitFieldWidth().toLong())
            return bitfield(name, type, sublayout.copy(name = name))
        } else if (cursor.isAnonymousStruct && type is Typed.Declared) {
            return type.reference
        } else {
            return field(cursor, name, type)
        }
    }

    open fun offsetOf(parent: Type, c: Cursor): Long {
        return if (c.kind === CursorKind.FIELD_DECL) {
            parent.getOffsetOf(c.spelling)
        } else {
            c.flattenableChildren()
                .map { child ->
                    offsetOf(
                        parent,
                        child
                    )
                }
                .firstOrNull() ?: error("Can not find offset of: $c, in: $parent")
        }
    }

    open fun fieldSize(c: Cursor): Long {
        if (c.type.kind == TypeKind.IncompleteArray) {
            return 0
        }
        return if (c.isBitField) c.getBitFieldWidth().toLong() else c.type.size() * 8
    }

    open fun bitfield(
        sublayouts: List<MemoryLayout?>,
        declarations: MutableList<Variable>
    ): Scoped {
        return bitfields(
            declarations[0].cursor,
            MemoryLayout.structLayout(sublayouts.filterNotNull().toTypedArray()),
            declarations
        )
    }
}

internal fun compute(typeMaker: TypeMaker, offsetInParent: Long, parent: Type, type: Type): Typed? {
    return computeInternal(typeMaker, offsetInParent, parent, type, "")
}

private fun computeAnonymous(
    typeMaker: TypeMaker,
    offsetInParent: Long,
    parent: Type,
    type: Type,
    name: String
): Typed? {
    return computeInternal(typeMaker, offsetInParent, parent, type, name)
}

private fun computeInternal(
    typeMaker: TypeMaker,
    offsetInParent: Long,
    parent: Type,
    type: Type,
    name: String
): Typed? {
    val cursor = type.declarationCursor.getDefinition()
    if (cursor.isInvalid) {
        return null
    }
    val isUnion = cursor.kind === CursorKind.UNION_DECL
    return if (isUnion) UnionLayoutComputer(
        typeMaker,
        offsetInParent,
        parent,
        type
    ).compute(name)
    else StructLayoutComputer(typeMaker, offsetInParent, parent, type).compute(name)
}