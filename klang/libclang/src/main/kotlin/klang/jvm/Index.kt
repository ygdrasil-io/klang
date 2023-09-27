package klang.jvm

import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.CXIndex
import klang.jvm.binding.CXTranslationUnit
import klang.jvm.binding.CXTranslationUnit_Flags.CXTranslationUnit_DetailedPreprocessingRecord
import klang.jvm.binding.CXTranslationUnit_Flags.CXTranslationUnit_ForSerialization
import klang.jvm.binding.CXTranslationUnit_Flags.CXTranslationUnit_KeepGoing
import klang.jvm.binding.CXTranslationUnit_Flags.CXTranslationUnit_None
import klang.jvm.binding.CXTranslationUnit_Flags.CXTranslationUnit_SingleFileParse
import klang.jvm.binding.NativeIndexerCallbacks

fun createIndex(excludeDeclarationsFromPCH: Boolean, displayDiagnostics: Boolean): Index {
    return Clang.createIndex(excludeDeclarationsFromPCH, displayDiagnostics)
        .let(::Index)
}

class Index(private val ptr: CXIndex) : AutoCloseable {

    private val translationUnits = mutableListOf<TranslationUnit>()

    class UnsavedFile(val file: String, val contents: String)

    @Throws(TranslationException::class)
    fun parseTranslationUnit(
        sourceFilename: String?,
        args: Array<String?>,
        vararg options: TranslationUnit.Flag
    ): TranslationUnit {
        val flags = buildOptionsMask(*options)
        val translationUnit = Clang.parseTranslationUnit(ptr, sourceFilename, args, args.size, null, 0, flags)
            ?: throw TranslationException()
        return TranslationUnit(NativePool.record(translationUnit))
    }

    @Throws(IndexException::class)
    fun indexSourceFile(
        callback: IndexerCallback, sourceFilename: String, args: Array<String>? = null,
        vararg options: TranslationUnit.Flag
    ): TranslationUnit {
        val action = NativePool.record(Clang.IndexAction_create(ptr))
        val callbacks = NativeIndexerCallbacks(callback)
        val flags = buildOptionsMask(*options)
        val tuRef = PointerByReference()
        val exitCode = Clang.indexSourceFile(
            action, null, callbacks, callbacks.size(), 0 /* TODO: CXIndexOptFlags */,
            sourceFilename, args, args?.size ?: 0, null, 0, tuRef, flags
        )
        if (exitCode != 0) {
            throw IndexException(exitCode)
        }
        val tu = CXTranslationUnit()
        tu.pointer = tuRef.value
        return TranslationUnit(NativePool.record(tu))
    }

    @Throws(TranslationException::class)
    fun parse(
        file: String,
        dh: (Diagnostic) -> Unit,
        detailedPreprocessorRecord: Boolean,
        args: List<String>
    ): TranslationUnit {
        return parseTU(file, dh, defaultOptions(detailedPreprocessorRecord), args)
    }

    fun parseTU(file: String, dh: (Diagnostic) -> Unit, options: List<Int>, args: List<String>): TranslationUnit {
        val flags = buildOptionsMask(options)
        val outAddress = CXTranslationUnit()
        val code =
            Clang.parseTranslationUnit2(
                ptr,
                file,
                args,
                args.size,
                null,
                0,
                flags,
                outAddress.pointer
            ).toUInt()
            .let(ErrorCode::of)
        val translationUnit = TranslationUnit(outAddress)
        // even if we failed to parse, we might still have diagnostics
        translationUnit.processDiagnostics(dh)
        if (code !== ErrorCode.Success) {
            error("Parsing failed with error: $code with file: $file")
        }
        translationUnits.add(translationUnit)
        return translationUnit
    }

    override fun close() {
        Clang.disposeIndex(ptr)
    }
}


private fun defaultOptions(detailedPreprocessorRecord: Boolean): List<Int> {
    var rv = CXTranslationUnit_ForSerialization
    rv = rv or CXTranslationUnit_KeepGoing
    rv = rv or CXTranslationUnit_SingleFileParse
    //rv |= CXTranslationUnit_SkipFunctionBodies();
    //rv |= CXTranslationUnit_IgnoreNonErrorsFromIncludedFiles();
    //if (detailedPreprocessorRecord) {
    rv = rv or CXTranslationUnit_DetailedPreprocessingRecord
    //}
    return listOf(CXTranslationUnit_None)
}