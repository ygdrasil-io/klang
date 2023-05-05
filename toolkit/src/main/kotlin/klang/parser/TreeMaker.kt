package klang.parser

import klang.jvm.Cursor
import klang.jvm.CursorKind
import klang.jvm.Type
import klang.parser.domain.MemoryLayout
import klang.parser.domain.ParsingContext
import klang.parser.domain.declaration.*
import klang.parser.domain.declaration.Function
import klang.parser.domain.type.*

class TreeMaker(private val context: ParsingContext) {

    var typeMaker = TypeMaker(this, context)

    fun freeze() {
        typeMaker.resolveTypeReferences()
    }

    private fun Cursor.collectAttributes(): Map<String, List<String>> {
        return children().filter { it.isAttribute }
            .groupBy({ it.kind.name }, { it.spelling })
    }


    fun createTree(cursor: Cursor): Declaration? {
        return cursor.createTreeInternal()
            ?.apply { attributes.putAll(cursor.collectAttributes()) }
    }

    private fun Cursor.createTreeInternal(): Declaration? {
        return when (kind) {
            CursorKind.ENUM_DECL -> createEnum()
            CursorKind.ENUM_CONSTANT_DECL -> error("should not be created from this method")
            CursorKind.FIELD_DECL -> if (isBitField) createBitfield() else createVariable(Variable.Kind.FIELD)
            CursorKind.PARM_DECL -> createVariable(Variable.Kind.PARAMETER)
            CursorKind.FUNCTION_DECL, CursorKind.CXX_METHOD -> createFunction()
            CursorKind.STRUCT_DECL -> createStruct()
            CursorKind.UNION_DECL -> createRecord(Scoped.Kind.UNION)
            CursorKind.TYPEDEF_DECL -> createTypedef()
            CursorKind.VAR_DECL -> createVariable(Variable.Kind.GLOBAL)
            CursorKind.NAMESPACE -> createNamespace()
            CursorKind.CLASS_DECL -> createClass()
            CursorKind.CXX_ACCESS_SPECIFIER -> createCXXAccessSpecifier()
            else -> {
                println("cursor not supported $kind at line ${sourceLocation?.fileLocation?.line}")
                null
            }
        }
    }

    private fun Cursor.createStruct(): Scoped {
        val type = compute(typeMaker, 0, type, type) as? Typed.Declared
            ?: error("fail to create record from ${this.sourceLocation?.fileLocation}")
        return if (isDefinition) {
            type.reference
        } else {
            error("unreachable state")
        }
    }

    private fun Cursor.createCXXAccessSpecifier(): Declaration = accessSpecifier(
        spelling,
        childrenTree().toMutableList()
    )

    private fun Cursor.childrenTree(): List<Declaration> = children().mapNotNull { createTree(it) }

    private fun Cursor.createClass(): Declaration = classDeclaration(
        spelling
    ).apply {
        typeMaker.declareClass(this, type)
        declarations.addAll(childrenTree())
    }

    private fun Cursor.createNamespace(): Declaration {
        return namespace(
            this,
            spelling,
            childrenTree().toMutableList()
        )
    }

    private fun Cursor.createTypedef(): Typedef? {
        val cursorType = toType()
        var canonicalType = canonicalType(cursorType)
        if (canonicalType is Typed.Declared) {
            val s = canonicalType.reference
            if (s.name == spelling) {
                // typedef record with the same name, no need to present twice
                return null
            }
        }
        var funcType: Lambda? = null
        var isFuncPtrType = false

        if (canonicalType is Lambda) {
            funcType = canonicalType
        } else if (canonicalType.isPointerType()) {
            var pointeeTyped: Typed? = null
            try {
                pointeeTyped = (canonicalType as Delegated).type
            } catch (npe: NullPointerException) {
                // exception thrown for unresolved pointee type. Ignore if we hit that case.
            }
            if (pointeeTyped is Lambda) {
                funcType = pointeeTyped
                isFuncPtrType = true
            }
        }
        if (funcType != null) {
            children()
                .filter { it.kind === CursorKind.PARM_DECL }
                .mapNotNull { createTree(it) }
                .map { it.name }
                .takeIf { it.isNotEmpty() }
                ?.let { params ->
                    canonicalType = funcType.withParameterNames(params)
                    if (isFuncPtrType) {
                        canonicalType = Pointer(canonicalType)
                    }
                }
        }
        return Typedef(canonicalType, spelling, this)
    }


    private fun Cursor.createRecord(
        scopeKind: Scoped.Kind
    ): Scoped? {
        val type = compute(typeMaker, 0, type, type) as? Typed.Declared
            ?: error("fail to create record from ${this.sourceLocation?.fileLocation}")
        val declarations = type.reference.declarations.filterNestedDeclarations()
        return if (isDefinition) {
            //just a declaration AND definition, we have a layout
            Declaration.scoped(
                scopeKind, this, spelling,
                type.reference.layout!!, declarations
            )
        } else {
            //if there's a real definition somewhere else, skip this redundant declaration
            if (!getDefinition().isInvalid) {
                null
            } else Declaration.scoped(
                scopeKind,
                this,
                spelling,
                declarations
            )
        }
    }

    private fun Cursor.createVariable(
        kind: Variable.Kind
    ): Variable? {
        checkCursorAny(CursorKind.VAR_DECL, CursorKind.FIELD_DECL, CursorKind.PARM_DECL)
        try {
            val type = toType()
            return variable(kind, this, spelling, type)
        } catch (ex: TypeMaker.TypeException) {
            println(ex)
            println("WARNING: ignoring variable: $spelling")
        }
        return null
    }

    private fun Cursor.createBitfield(): Variable {
        checkCursorAny(CursorKind.FIELD_DECL)
        return bitfield(
            spelling, toType(),
            MemoryLayout(getBitFieldWidth().toLong())
        )
    }

    private fun Cursor.createEnumConstant(type: Type): Constant {
        return Constant(
            typeMaker.makeType(type),
            getEnumConstantValue(),
            spelling,
            this,
        )
    }

    private fun Cursor.createEnum(): Scoped? {
        val declarations = children()
            .filter {
                when {
                    // only non-empty and named bit fields are generated
                    it.isBitField -> it.getBitFieldWidth() != 0 && it.spelling.isNotEmpty()
                    else -> true
                }
            }
            .map { createEnumConstant(enumDeclIntegerType) }
            .filterNestedDeclarations()

        return when {
            isDefinition -> {
                //just a declaration AND definition, we have a layout
                val layout = valueLayoutForSize(type.size() * 8).layout ?: error("")
                Declaration.enumeration(this, spelling, layout, declarations)
            }
            else -> {
                //just a declaration
                //if there's a real definition somewhere else, skip this redundant declaration
                if (!getDefinition().isInvalid) {
                    null
                } else Declaration.enumeration(this, spelling, declarations)
            }
        }
    }

    private fun Cursor.createFunction(): Function {
        val parameters = (0 until numberOfArgs()).map {
            createTree(getArgument(it.toUInt()))
                .let { declaration -> declaration as? Variable }
                ?: error("error at ${sourceLocation?.fileLocation}")
        }
        val type = toType()
        val funcType = canonicalType(type)
        return Function(
            funcType as Lambda, parameters, spelling, this
        )
    }

    private fun List<Declaration>.filterNestedDeclarations(): List<Declaration> {
        return filter {
            isEnum(it) || it.name.isNotEmpty() || isAnonymousStruct(it) || isBitfield(it)
        }
    }

    private fun isBitfield(scoped: Declaration): Boolean {
        return scoped is Scoped &&
                scoped.kind == Scoped.Kind.BITFIELDS
    }


    private fun isAnonymousStruct(declaration: Declaration): Boolean {
        return declaration.cursor.isAnonymousStruct
    }

    private fun isEnum(scoped: Declaration): Boolean {
        return scoped is Scoped &&
                scoped.kind == Scoped.Kind.ENUM
    }

    private fun canonicalType(delegated: Typed): Typed {
        return if (delegated is Delegated &&
            delegated.kind == Delegated.Kind.TYPEDEF
        ) {
            delegated.type
        } else {
            delegated
        }
    }

    private fun Cursor.toType(): Typed {
        return typeMaker.makeType(type)
    }

    private fun valueLayoutForSize(size: Long): Primitive.Kind {
        return when (size.toInt()) {
            8 -> Primitive.Kind.Char
            16 -> Primitive.Kind.Short
            32 -> Primitive.Kind.Int
            64 -> Primitive.Kind.LongLong
            else -> error("Cannot infer container layout")
        }
    }

}

private fun Cursor.checkCursorAny(vararg kinds: CursorKind) {
    for (k in kinds) {
        if (k == kind) {
            return
        }
    }
    throw IllegalArgumentException("Invalid cursor kind")
}


fun Cursor.createMacro(name: String, typed: Typed, value: Any): Constant {
    checkCursorAny(CursorKind.MACRO_DEFINITION)
    return Constant(typed, value, name, this)
}