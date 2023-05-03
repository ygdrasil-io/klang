package klang.jvm

import com.sun.jna.PointerType
import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.NativeIndexerCallbacks

class Index : PointerType() {
    @Throws(TranslationException::class)
    fun parseTranslationUnit(
        sourceFilename: String?,
        args: Array<String?>,
        vararg options: TranslationUnit.Flag
    ): TranslationUnit {
        val flags = buildOptionsMask(*options)
        val translationUnit = Clang.parseTranslationUnit(this, sourceFilename, args, args.size, null, 0, flags)
            ?: throw TranslationException()
        return NativePool.record(translationUnit)
    }

    @Throws(IndexException::class)
    fun indexSourceFile(
        callback: IndexerCallback, sourceFilename: String?, args: Array<String?>,
        vararg options: TranslationUnit.Flag
    ): TranslationUnit {
        val action = NativePool.record(Clang.IndexAction_create(this))
        val callbacks = NativeIndexerCallbacks(callback)
        val flags = buildOptionsMask(*options)
        val tuRef = PointerByReference()
        val exitCode = Clang.indexSourceFile(
            action, null, callbacks, callbacks.size(), 0 /* TODO: CXIndexOptFlags */,
            sourceFilename, args, args.size, null, 0, tuRef, flags
        )
        if (exitCode != 0) {
            throw IndexException(exitCode)
        }
        val tu = TranslationUnit()
        tu.pointer = tuRef.value
        return NativePool.record(tu)
    }
}