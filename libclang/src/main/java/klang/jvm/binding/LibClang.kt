package klang.jvm.binding

import com.sun.jna.Library
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import klang.jvm.Diagnostic
import klang.jvm.Index
import klang.jvm.TranslationUnit

interface LibClang : Library {
    fun getClangVersion(): String
    fun getFileName(file: CXFile): String
    fun getCursorSpelling(cursor: CXCursor.CXCursorByValue): String
    fun getCursorKindSpelling(kind: Int): String
    fun getCursorType(cursor: CXCursor.CXCursorByValue): CXType.ByValue
    fun getTypeKindSpelling(kind: Int): String
    fun createIndex(excludeDeclarationsFromPCH: Boolean, displayDiagnostics: Boolean): Index
    fun disposeIndex(index: Index)

    // TODO: class CXUnsavedFile extends Structure
    fun parseTranslationUnit(
        index: Index, sourceFilename: String?, commandLineArgs: Array<String?>?,
        numCommandLineArgs: Int, unsavedFiles: Void?, numUnsavedFiles: Int, options: Int
    ): TranslationUnit?

    fun disposeTranslationUnit(translationUnit: TranslationUnit)
    fun IndexAction_create(index: Index): CXIndexAction
    fun IndexAction_dispose(action: CXIndexAction)
    fun indexSourceFile(
        action: CXIndexAction, clientData: Void?, indexCallbacks: NativeIndexerCallbacks,
        indexCallbacksSize: Int, indexOptions: Int, sourceFilename: String?, commandLineArgs: Array<String?>?,
        numCommandLineArgs: Int, unsavedFiles: Void?, numUnsavedFiles: Int,
        /* CXTranslationUnit */translationUnit: PointerByReference?, tuOptions: Int
    ): Int

    fun indexLoc_getFileLocation(
        loc: CXIdxLoc.ByValue,  /* CXIdxClientFile */indexFile: PointerByReference?,
        /* CXFile */file: PointerByReference?, line: IntByReference?,
        column: IntByReference?, offset: IntByReference?
    )

    fun getNumDiagnostics(unit: TranslationUnit): Int
    fun getDiagnostic(unit: TranslationUnit, index: Int): Diagnostic
    fun disposeDiagnostic(diagnostic: Diagnostic)
    fun getDiagnosticSeverity(diagnostic: Diagnostic): Int
    fun formatDiagnostic(diagnostic: Diagnostic, options: Int): String

}