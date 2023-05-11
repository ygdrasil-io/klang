

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXString;

import java.util.ArrayList;
import java.util.List;

/* package */ class NativePool {
    public static final NativePool I = new NativePool();

    private final List<CXString.ByValue> strings = new ArrayList<CXString.ByValue>();
    private final List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
    private final List<Diagnostic> diagnostics = new ArrayList<Diagnostic>();
    private final List<CXIndexAction> indexActions = new ArrayList<CXIndexAction>();
    private final List<Index> indexes = new ArrayList<Index>();

    private NativePool() {}

    public void record(@NotNull CXString.ByValue string) {
        strings.add(string);
    }

    @NotNull
    public TranslationUnit record(@NotNull TranslationUnit translationUnit) {
        translationUnits.add(translationUnit);
        return translationUnit;
    }

    @NotNull
    public Diagnostic record(@NotNull Diagnostic diagnostic) {
        diagnostics.add(diagnostic);
        return diagnostic;
    }

    @NotNull
    public CXIndexAction record(@NotNull CXIndexAction indexAction) {
        indexActions.add(indexAction);
        return indexAction;
    }

    @NotNull
    public Index record(@NotNull Index index) {
        indexes.add(index);
        return index;
    }

    public void disposeAll() {
        LibClang lib = LibClang.I;
        for (CXString.ByValue string : strings) {
            lib.disposeString(string);
        }
        for (TranslationUnit translationUnit : translationUnits) {
            lib.disposeTranslationUnit(translationUnit);
        }
        for (Diagnostic diagnostic : diagnostics) {
            lib.disposeDiagnostic(diagnostic);
        }
        for (CXIndexAction indexAction : indexActions) {
            lib.IndexAction_dispose(indexAction);
        }
        for (Index index : indexes) {
            lib.disposeIndex(index);
        }

        strings.clear();
        translationUnits.clear();
        diagnostics.clear();
        indexActions.clear();
        indexes.clear();
    }
}
