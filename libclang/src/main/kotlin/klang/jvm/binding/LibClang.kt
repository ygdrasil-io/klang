package klang.jvm.binding

import com.sun.jna.Library
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import klang.jvm.Diagnostic

interface LibClang : Library {
    fun getClangVersion(): String
    fun getFileName(file: CXFile): String
    fun getCursorSpelling(cursor: CXCursor.CXCursorByValue): String
    fun getCursorKindSpelling(kind: Int): String
    fun getCursorType(cursor: CXCursor.CXCursorByValue): CXType.ByValue
    fun getTypeKindSpelling(kind: Int): String
    fun createIndex(excludeDeclarationsFromPCH: Boolean, displayDiagnostics: Boolean): CXIndex
    fun disposeIndex(index: CXIndex)

    // TODO: class CXUnsavedFile extends Structure
    fun parseTranslationUnit(
        index: CXIndex, sourceFilename: String?, commandLineArgs: Array<String?>?,
        numCommandLineArgs: Int, unsavedFiles: Void?, numUnsavedFiles: Int, options: Int
    ): CXTranslationUnit?

    fun disposeTranslationUnit(translationUnit: CXTranslationUnit)
    fun IndexAction_create(index: CXIndex): CXIndexAction
    fun IndexAction_dispose(action: CXIndexAction)
    fun indexSourceFile(
        action: CXIndexAction, clientData: Void?, indexCallbacks: NativeIndexerCallbacks,
        indexCallbacksSize: Int, indexOptions: Int, sourceFilename: String?, commandLineArgs: Array<String>?,
        numCommandLineArgs: Int, unsavedFiles: Void?, numUnsavedFiles: Int,
        /* CXTranslationUnit */translationUnit: PointerByReference?, tuOptions: Int
    ): Int

    fun indexLoc_getFileLocation(
        loc: CXIdxLoc.ByValue,  /* CXIdxClientFile */indexFile: PointerByReference?,
        /* CXFile */file: PointerByReference?, line: IntByReference?,
        column: IntByReference?, offset: IntByReference?
    )

    fun getNumDiagnostics(unit: CXTranslationUnit): Int
    fun getDiagnostic(unit: CXTranslationUnit, index: Int): Diagnostic
    fun disposeDiagnostic(diagnostic: Diagnostic)
    fun getDiagnosticSeverity(diagnostic: Diagnostic): Int
    fun formatDiagnostic(diagnostic: Diagnostic, options: Int): String
    fun Cursor_isMacroFunctionLike(cursor: CXCursor.CXCursorByValue): Int
    fun saveTranslationUnit(translationUnit: CXTranslationUnit, path: String, uInt: Int): Int
    fun parseTranslationUnit2(
        index: CXIndex,
        file: String,
        args: List<String>,
        size: Int,
        nothing: PointerByReference?,
        i: Int,
        flags: Any,
        pointer: Pointer
    ): Int

    fun getDiagnosticSpelling(diagnostic: Diagnostic): String
    fun reparseTranslationUnit(
        TU: CXTranslationUnit,
        num_unsaved_files: Int,
        unsaved_files: Array<CXUnsavedFile>?,
        options: Int
    ) : Int

    fun defaultReparseOptions(pointer: CXTranslationUnit): Int
    fun getTranslationUnitCursor(pointer: CXTranslationUnit): CXCursor.CXCursorByValue
    fun visitChildren(
        parrent: CXCursor.CXCursorByValue,
        visitor: CXCursorVisitor,
        client_data: Pointer?
    )

    fun Cursor_Evaluate(cursor: CXCursor.CXCursorByValue): CXEvalResult?
    fun EvalResult_dispose(ptr: CXEvalResult)
    fun EvalResult_getKind(ptr: CXEvalResult): Int
    fun EvalResult_getAsDouble(ptr: CXEvalResult): Long
    fun EvalResult_getAsFloat(ptr: CXEvalResult): Double
    fun EvalResult_getAsStr(ptr: CXEvalResult): String
    fun getCanonicalType(type: CXType.ByValue): CXType.ByValue
    fun getCursorLocation(cursor: CXCursor.CXCursorByValue): CXSourceLocationByValue
    fun getNullLocation(): CXSourceLocationByValue
    fun equalLocations(location: CXSourceLocationByValue, nullLocation: CXSourceLocationByValue): Int

    fun getFileLocation(
        location: CXSourceLocationByValue,
        file: CXFile,
        line: IntByReference,
        column: IntByReference,
        offset: IntByReference
    )

    fun isDeclaration(ordinal: Int): Int
    fun isPreprocessing(ordinal: Int): Int
    fun isAttribute(ordinal: Int): Int
    fun isCursorDefinition(cursor: CXCursor.CXCursorByValue): Int
    fun getCursorDefinition(cursor: CXCursor.CXCursorByValue): CXCursor.CXCursorByValue
    fun Cursor_isBitField(cursor: CXCursor.CXCursorByValue): Int
    fun getFieldDeclBitWidth(cursor: CXCursor.CXCursorByValue): Int
    fun Type_getSizeOf(type: CXType.ByValue): Long
    fun isInvalid(ordinal: Int): Int
    fun Cursor_getNumArguments(cursor: CXCursor.CXCursorByValue): Int
    fun Cursor_getArgument(cursor: CXCursor.CXCursorByValue, idx: Int): CXCursor.CXCursorByValue
    fun getEnumConstantDeclValue(cursor: CXCursor.CXCursorByValue): Long
    fun getEnumDeclIntegerType(cursor: CXCursor.CXCursorByValue): CXType.ByValue
    fun getCursorExtent(cursor: CXCursor.CXCursorByValue): CXSourceRangeByVale
    fun Range_isNull(range: CXSourceRangeByVale): Int
    fun Cursor_getTranslationUnit(cursor: CXCursor.CXCursorByValue): CXTranslationUnit
    fun tokenize(pointer: CXTranslationUnit, range: CXSourceRangeByVale, Tokens: Pointer, NumTokens: Pointer)
    fun PrintingPolicy_dispose(policy: CXPrintingPolicy)
    fun PrintingPolicy_getProperty(policy: CXPrintingPolicy, ordinal: Int): Int
    fun getCursorPrettyPrinted(cursor: CXCursor.CXCursorByValue, policy: CXPrintingPolicy): String
    fun getCursorPrintingPolicy(cursor: CXCursor.CXCursorByValue): CXPrintingPolicy
    fun getTokenSpelling(pointer: CXTranslationUnit, token: CXToken): String
    fun getTypeDeclaration(type: CXType.ByValue): CXCursor.CXCursorByValue
    fun Cursor_isAnonymousRecordDecl(cursor: CXCursor.CXCursorByValue): Int
    fun Type_getOffsetOf(type: CXType.ByValue, fieldName: String): Long
    fun getCursorSemanticParent(cursor: CXCursor.CXCursorByValue): CXCursor.CXCursorByValue
    fun isFunctionTypeVariadic(type: CXType.ByValue): Int
    fun equalTypes(type: CXType.ByValue, type1: CXType.ByValue): Int
    fun getTypeSpelling(type: CXType.ByValue): String
    fun getNumElements(type: CXType.ByValue): Long
    fun getElementType(type: CXType.ByValue): CXType.ByValue
    fun getNumArgTypes(type: CXType.ByValue): Int
    fun getArgType(type: CXType.ByValue, idx: Int): CXType.ByValue
    fun getResultType(type: CXType.ByValue): CXType.ByValue
    fun getPointeeType(type: CXType.ByValue): CXType.ByValue
	fun getTypedefDeclUnderlyingType(cursor: CXCursor.CXCursorByValue): CXType.ByValue

}
