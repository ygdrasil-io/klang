

package jclang;

import com.sun.jna.PointerType;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jclang.structs.NativeIndexerCallbacks;
import jclang.util.Util;

import static jclang.TranslationUnit.Flag;

public class Index extends PointerType {
    @NotNull
    public TranslationUnit parseTranslationUnit(@Nullable String sourceFilename, @NotNull String[] args, @NotNull Flag... options)
            throws TranslationException {
        int flags = Util.buildOptionsMask(options);
        TranslationUnit translationUnit = LibClang.I.parseTranslationUnit(this, sourceFilename, args, args.length, null, 0, flags);
        if (translationUnit == null) {
            throw new TranslationException();
        }
        return NativePool.I.record(translationUnit);
    }

    @NotNull
    public TranslationUnit indexSourceFile(@NotNull IndexerCallback callback, @Nullable String sourceFilename, @NotNull String[] args,
            @NotNull Flag... options) throws IndexException {
        CXIndexAction action = NativePool.I.record(LibClang.I.IndexAction_create(this));
        NativeIndexerCallbacks callbacks = new NativeIndexerCallbacks(callback);
        int flags = Util.buildOptionsMask(options);
        PointerByReference tuRef = new PointerByReference();
        int exitCode = LibClang.I.indexSourceFile(action, null, callbacks, callbacks.size(), 0 /* TODO: CXIndexOptFlags */,
                sourceFilename, args, args.length, null, 0, tuRef, flags);
        if (exitCode != 0) {
            throw new IndexException(exitCode);
        }
        TranslationUnit tu = new TranslationUnit();
        tu.setPointer(tuRef.getValue());
        return NativePool.I.record(tu);
    }
}
