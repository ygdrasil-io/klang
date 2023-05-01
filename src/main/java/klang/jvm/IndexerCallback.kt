package klang.jvm

import java.io.File

interface IndexerCallback {
    fun enteredMainFile(mainFile: File)
    fun startedTranslationUnit()
    fun indexDeclaration(info: DeclarationInfo)

    companion object {
        val DO_NOTHING: IndexerCallback = object : AbstractIndexerCallback() {}
    }
}