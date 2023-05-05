package klang.jvm

import klang.jvm.binding.CXIndex
import klang.jvm.binding.CXIndexAction
import klang.jvm.binding.CXTranslationUnit

object NativePool {
    private val translationUnits = mutableListOf<CXTranslationUnit>()
    private val diagnostics = mutableListOf<Diagnostic>()
    private val indexActions = mutableListOf<CXIndexAction>()
    private val indexes = mutableListOf<CXIndex>()

    fun record(translationUnit: CXTranslationUnit): CXTranslationUnit {
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

    fun record(index: CXIndex): CXIndex {
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