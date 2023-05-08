

package jclang;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface IndexerCallback {
    IndexerCallback DO_NOTHING = new AbstractIndexerCallback() {};

    void enteredMainFile(@NotNull File mainFile);

    void startedTranslationUnit();

    void indexDeclaration(@NotNull DeclarationInfo info);
}
