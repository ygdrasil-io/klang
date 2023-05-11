

package jclang;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import jclang.structs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/* package */ interface LibClang extends Library {
    LibClang I = Native.loadLibrary("clang-9.0.1", LibClang.class, new HashMap<>() {{
        put(Library.OPTION_FUNCTION_MAPPER, (FunctionMapper) (library, method) -> "clang_" + method.getName());
    }});

    @NotNull
    String getCString(@NotNull CXString.ByValue string);

    void disposeString(@NotNull CXString.ByValue string);


    @NotNull
    CXString.ByValue getClangVersion();


    @NotNull
    CXString.ByValue getFileName(@NotNull CXFile file);


    @NotNull
    CXString.ByValue getCursorSpelling(@NotNull CXCursor.ByValue cursor);

    @NotNull
    CXString.ByValue getCursorKindSpelling(int kind);

    @NotNull
    CXType.ByValue getCursorType(@NotNull CXCursor.ByValue cursor);


    @NotNull
    CXString.ByValue getTypeKindSpelling(int kind);


    @NotNull
    Index createIndex(boolean excludeDeclarationsFromPCH, boolean displayDiagnostics);

    void disposeIndex(@NotNull Index index);

    // TODO: class CXUnsavedFile extends Structure
    @Nullable
    TranslationUnit parseTranslationUnit(@NotNull Index index, @Nullable String sourceFilename, @Nullable String[] commandLineArgs,
                                         int numCommandLineArgs, @Nullable Void unsavedFiles, int numUnsavedFiles, int options);

    void disposeTranslationUnit(@NotNull TranslationUnit translationUnit);


    @NotNull
    CXIndexAction IndexAction_create(@NotNull Index index);

    void IndexAction_dispose(@NotNull CXIndexAction action);

    int indexSourceFile(@NotNull CXIndexAction action, @Nullable Void clientData, @NotNull NativeIndexerCallbacks indexCallbacks,
                        int indexCallbacksSize, int indexOptions, @Nullable String sourceFilename, @Nullable String[] commandLineArgs,
                        int numCommandLineArgs, @Nullable Void unsavedFiles, int numUnsavedFiles,
                        @Nullable PointerByReference /* CXTranslationUnit */ translationUnit, int tuOptions);


    void indexLoc_getFileLocation(@NotNull CXIdxLoc.ByValue loc, @Nullable PointerByReference /* CXIdxClientFile */ indexFile,
                                  @Nullable PointerByReference /* CXFile */ file, @Nullable IntByReference line,
                                  @Nullable IntByReference column, @Nullable IntByReference offset);


    int getNumDiagnostics(@NotNull TranslationUnit unit);

    @NotNull
    Diagnostic getDiagnostic(@NotNull TranslationUnit unit, int index);

    void disposeDiagnostic(@NotNull Diagnostic diagnostic);

    int getDiagnosticSeverity(@NotNull Diagnostic diagnostic);

    @NotNull
    CXString.ByValue formatDiagnostic(@NotNull Diagnostic diagnostic, int options);
}
