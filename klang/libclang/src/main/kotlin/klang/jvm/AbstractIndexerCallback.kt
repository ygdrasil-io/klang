package klang.jvm

import java.io.File

abstract class AbstractIndexerCallback : IndexerCallback {
    override fun enteredMainFile(mainFile: File) {}
    override fun startedTranslationUnit() {}
    override fun indexDeclaration(info: DeclarationInfo) {}
}