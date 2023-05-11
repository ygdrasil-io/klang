

package jclang.structs;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jclang.CXFile;
import jclang.DeclarationInfo;
import jclang.IndexerCallback;

@SuppressWarnings("unused")
@Structure.FieldOrder({"abortQuery", "diagnostic", "enteredMainFile", "ppIncludedFile", "importedASTFile",
        "startedTranslationUnit", "indexDeclaration", "indexEntityReference"})
public class NativeIndexerCallbacks extends Structure {
    // TODO: all callbacks
    public Callback abortQuery;
    public Callback diagnostic;
    public EnteredMainFileCallback enteredMainFile;
    public Callback ppIncludedFile;
    public Callback importedASTFile;
    public StartedTranslationUnitCallback startedTranslationUnit;
    public Callback indexDeclaration;
    public Callback indexEntityReference;

    public NativeIndexerCallbacks(@NotNull final IndexerCallback callback) {
        super();
        this.enteredMainFile = (clientData, mainFile, reserved) -> {
            callback.enteredMainFile(mainFile.toFile());
            return null;
        };
        this.startedTranslationUnit = (clientData, reserved) -> callback.startedTranslationUnit();
        this.indexDeclaration = (IndexDeclarationCallback) (clientData, info) -> callback.indexDeclaration(new DeclarationInfo(info));
    }

    public interface EnteredMainFileCallback extends Callback {
        @Nullable
        Pointer apply(@Nullable Pointer clientData, @NotNull CXFile mainFile, @Nullable Pointer reserved);
    }

    public interface StartedTranslationUnitCallback extends Callback {
        void apply(@Nullable Pointer clientData, @Nullable Pointer reserved);
    }

    public interface IndexDeclarationCallback extends Callback {
        void apply(@Nullable Pointer clientData, @NotNull CXIdxDeclInfo.ByReference info);
    }
}
