package klang.parser

import klang.jvm.Type
import klang.jvm.TypeKind
import klang.parser.domain.ParsingContext
import klang.parser.domain.declaration.Scoped
import klang.parser.domain.type.*
import klang.parser.domain.type.Array

class TypeMaker(private val treeMaker: TreeMaker, val parsingContext: ParsingContext) {

    inner class ClangTypeReference(private var origin: Type) : Supplier<Typed> {

        private var derived: Typed = parsingContext.findTyped(origin)

        val isUnresolved = derived is Typed.Unresolved

        fun resolve() {
            derived = makeType(origin)
        }

        override fun get(): Typed {
            return derived
        }
    }

    private fun reference(typed: Type): ClangTypeReference {
        val ref = ClangTypeReference(typed)
        if (ref.isUnresolved) {
            parsingContext.unresolved.add(ref)
        }
        return ref
    }

    /**
     * Resolve all type references. This method should be called before discard clang cursors/types
     */
    fun resolveTypeReferences() {
        var resolving = parsingContext.unresolved
        parsingContext.unresolved = mutableListOf()
        while (resolving.isNotEmpty()) {
            resolving.forEach { it.resolve() }
            resolving = parsingContext.unresolved
            parsingContext.unresolved = mutableListOf()
        }
    }

    fun makeType(type: Type): Typed {
        return parsingContext.findTyped(type).let {
            if (it is Typed.Unresolved) {
                makeTypeInternal(type).apply { parsingContext.addTyped(type, this) }
            } else it
        }
    }

    fun declareClass(scopeClass: Scoped, type: Type) {
        assert(type.kind == TypeKind.Record) { "illegal state with kind ${type.kind} instead of ${TypeKind.Record}" }
        parsingContext.addTyped(type, Typed.Declared(scopeClass.cursor.fullName, scopeClass))
    }

    class TypeException internal constructor(message: String?) : RuntimeException(message)

    private fun makeTypeInternal(type: Type): Typed {
        return when (type.kind) {
            TypeKind.Auto -> parsingContext.findTyped(type.canonicalType)
            TypeKind.Void -> void()
            TypeKind.NullPtr -> nullPointer()
            TypeKind.CharS, TypeKind.CharU -> Primitive(Primitive.Kind.Char)
            TypeKind.Short -> Primitive(Primitive.Kind.Short)
            TypeKind.Int -> Primitive(Primitive.Kind.Int)
            TypeKind.Long -> Primitive(Primitive.Kind.Long)
            TypeKind.LongLong -> Primitive(Primitive.Kind.LongLong)
            TypeKind.SChar -> Qualified(Delegated.Kind.SIGNED, Primitive(Primitive.Kind.Char))
            TypeKind.UShort -> Qualified(Delegated.Kind.UNSIGNED, Primitive(Primitive.Kind.Short))
            TypeKind.UInt -> Qualified(Delegated.Kind.UNSIGNED, Primitive(Primitive.Kind.Int))
            TypeKind.ULong -> Qualified(Delegated.Kind.UNSIGNED, Primitive(Primitive.Kind.Long))
            TypeKind.ULongLong -> Qualified(Delegated.Kind.UNSIGNED, Primitive(Primitive.Kind.LongLong))
            TypeKind.UChar -> Qualified(Delegated.Kind.UNSIGNED, Primitive(Primitive.Kind.Char))
            TypeKind.Bool -> Primitive(Primitive.Kind.Bool)
            TypeKind.Double -> Primitive(Primitive.Kind.Double)
            TypeKind.Float -> Primitive(Primitive.Kind.Float)
            TypeKind.Unexposed, TypeKind.Elaborated -> type.canonicalType.let {
                if (it.equalType(type)) {
                    throw TypeException("Unknown type with same canonical type: " + type.spelling)
                }
                parsingContext.findTyped(it)
            }
            TypeKind.CONSTANT_ARRAY -> Array.Constant(
                type.numberOfElements,
                parsingContext.findTyped(type.elementType)
            )
            TypeKind.IncompleteArray -> Array.Incomplete(parsingContext.findTyped(type.elementType))
            TypeKind.Enum, TypeKind.Record -> Typed.Declared(
                type.declarationCursor.fullName,
                treeMaker.createTree(type.declarationCursor) as Scoped
            )
            TypeKind.BlockPointer, TypeKind.Pointer ->
				reference(type.pointeeType)
                        .apply { if (isUnresolved) resolve() }
                        .get()
                		.let { Pointer(it) }
            TypeKind.Typedef -> typedef(
                type.spelling,
                parsingContext.findTyped(type.canonicalType)
            )

            TypeKind.Complex -> Qualified(
                Delegated.Kind.COMPLEX,
                parsingContext.findTyped(type.elementType)
            )
            TypeKind.Vector -> Array.Vector(
                type.numberOfElements,
                parsingContext.findTyped(type.elementType)
            )
            TypeKind.WChar -> Primitive(Primitive.Kind.WChar)
            TypeKind.Char16 -> Primitive(Primitive.Kind.Char16)
            TypeKind.Int128 -> Primitive(Primitive.Kind.Int128)
            TypeKind.LongDouble -> Primitive(Primitive.Kind.LongDouble)
            TypeKind.UInt128 -> {
                val iTyped = Primitive(Primitive.Kind.Int128)
                Qualified(Delegated.Kind.UNSIGNED, iTyped)
            }
            else -> Typed.Error()
        }
    }

    private fun lowerFunctionType(t: Type): Typed {
        return makeType(t)
            .lowerFunctionType()
    }

    private fun Typed.lowerFunctionType(): Typed {
        return when (this) {
            is Array -> Pointer(elementTyped)
            is Delegated -> if (kind === Delegated.Kind.TYPEDEF && type is Array) {
                type.lowerFunctionType()
            } else this
            else -> this
        }

    }

    companion object {
        fun valueLayoutForSize(size: Long): Primitive.Kind {
            return when (size.toInt()) {
                8 -> Primitive.Kind.Char
                16 -> Primitive.Kind.Short
                32 -> Primitive.Kind.Int
                64 -> Primitive.Kind.LongLong
                else -> error("Cannot infer container layout")
            }
        }
    }
}