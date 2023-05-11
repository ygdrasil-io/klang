

package jclang;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class AbstractIndexerCallback implements IndexerCallback {
    @Override
    public void enteredMainFile(@NotNull File mainFile) {
    }

    @Override
    public void startedTranslationUnit() {
    }

    @Override
    public void indexDeclaration(@NotNull DeclarationInfo info) {
    }
}
