package klang.jvm

import com.sun.jna.Pointer
import klang.jvm.binding.CXChildVisitResult
import klang.jvm.binding.CXCursor
import klang.jvm.binding.CXCursorVisitor

class Cursor(private val cursor: CXCursor.CXCursorByValue) {

    val fullName: String
        get() = parentName() + spelling
    val isAnonymousStruct: Boolean
        get() = Clang.Cursor_isAnonymousRecordDecl(cursor) != 0
    val translationUnit: TranslationUnit
        get() = TranslationUnit(Clang.Cursor_getTranslationUnit(cursor))
    val enumDeclIntegerType: Type
        get() = Type(Clang.getEnumDeclIntegerType(cursor))
    val isInvalid: Boolean
        get() = Clang.isInvalid(kind.ordinal) != 0
    val isBitField: Boolean
        get() = Clang.Cursor_isBitField(cursor) != 0
    val isDefinition: Boolean
        get() = Clang.isCursorDefinition(cursor) != 0
    val isAttribute: Boolean
        get() = Clang.isAttribute(kind.ordinal) != 0
    val isPreprocessing: Boolean
        get() = Clang.isPreprocessing(kind.ordinal) != 0
    val isMacro: Boolean
        get() = isPreprocessing && kind == CursorKind.MACRO_DEFINITION
    val isDeclaration: Boolean
        get() = Clang.isDeclaration(kind.ordinal) != 0
	val isMacroFunctionLike: Boolean
		get() = Clang.Cursor_isMacroFunctionLike(cursor) != 0
    val sourceLocation: SourceLocation?
        get() = Clang.getCursorLocation(cursor)
            .let {
                if (Clang.equalLocations(it, Clang.getNullLocation()) != 0) null
                else SourceLocation(it)
            }
	val underlyingType: Type
		get() = Type(Clang.getTypedefDeclUnderlyingType(cursor))
    val spelling: String
        get() = Clang.getCursorSpelling(cursor)
    val kind: CursorKind // We should've called clang_getCursorKind here, but this works and is more efficient
        get() = CursorKind.fromNative(cursor.kind)
    val type: Type
        get() = Type(Clang.getCursorType(cursor))
    val semanticParent
        get() = Clang.getCursorSemanticParent(cursor)
            .let(::Cursor)
            .takeIf { it.cursor !== cursor && it.kind != CursorKind.TRANSLATION_UNIT }
    val extent: SourceRange?
        get() = Clang.getCursorExtent(cursor)
            .takeIf { Clang.Range_isNull(it) != 0 }
            ?.let(::SourceRange)

    fun children(): MutableList<Cursor> {

        val children = mutableListOf<Cursor>()

        val visitCursor = CXCursorVisitor { cursor, parent, data ->
            children.add(Cursor(cursor))
            CXChildVisitResult.CXChildVisit_Continue
        }

        Clang.visitChildren(cursor, visitCursor, null)
        return children
    }

    fun evaluate(): EvalResult? {
        return Clang.Cursor_Evaluate(cursor)
            ?.let { EvalResult(it) }
    }

    fun getDefinition(): Cursor {
        return Cursor(Clang.getCursorDefinition(cursor))
    }

    fun getBitFieldWidth(): Int {
        return Clang.getFieldDeclBitWidth(cursor)
    }

    fun numberOfArgs(): Int {
        return Clang.Cursor_getNumArguments(cursor)
    }

    fun getArgument(idx: UInt): Cursor {
        return Cursor(Clang.Cursor_getArgument(cursor, idx.toInt()))
    }

    // C long long, 64-bit
    fun getEnumConstantValue(): Long {
        return Clang.getEnumConstantDeclValue(cursor)
    }

    fun prettyPrinted(policy: PrintingPolicy): String {
        return Clang.getCursorPrettyPrinted(
            cursor,
            policy.policy
        )
    }

    fun prettyPrinted(): String {
        getPrintingPolicy()
            .use { policy -> return prettyPrinted(policy) }
    }

    fun getPrintingPolicy(): PrintingPolicy {
        return PrintingPolicy(Clang.getCursorPrintingPolicy(cursor))
    }

    fun parentName(): String = semanticParent?.let { it.parentName() + it.spelling + "::" } ?: ""
}