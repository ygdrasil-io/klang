package klang.jvm

import klang.jvm.binding.CXIndexAction

object NativePool {
    private val translationUnits: MutableList<TranslationUnit> = mutableListOf()
    private val diagnostics: MutableList<Diagnostic> = mutableListOf()
    private val indexActions: MutableList<CXIndexAction> = mutableListOf()
    private val indexes: MutableList<Index> = mutableListOf()

    fun record(translationUnit: TranslationUnit): TranslationUnit {
        translationUnits.add(translationUnit)
        return translationUnit
    }

    fun record(diagnostic: Diagnostic): Diagnostic {
        diagnostics.add(diagnostic)
        return diagnostic
    }

    fun record(indexAction: CXIndexAction): CXIndexAction {
        indexActions.add(indexAction)
        return indexAction
    }

    fun record(index: Index): Index {
        indexes.add(index)
        return index
    }

    fun disposeAll() {
        val lib = Clang
        for (translationUnit in translationUnits) {
            lib.disposeTranslationUnit(translationUnit)
        }
        for (diagnostic in diagnostics) {
            lib.disposeDiagnostic(diagnostic)
        }
        for (indexAction in indexActions) {
            lib.IndexAction_dispose(indexAction)
        }
        for (index in indexes) {
            lib.disposeIndex(index)
        }
        translationUnits.clear()
        diagnostics.clear()
        indexActions.clear()
        indexes.clear()
    }
}