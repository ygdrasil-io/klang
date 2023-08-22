package klang.jvm.binding

import com.sun.jna.Callback
import com.sun.jna.Pointer
import com.sun.jna.Structure
import klang.jvm.DeclarationInfo
import klang.jvm.IndexerCallback

@Suppress("unused")
@Structure.FieldOrder(
    "abortQuery",
    "diagnostic",
    "enteredMainFile",
    "ppIncludedFile",
    "importedASTFile",
    "startedTranslationUnit",
    "indexDeclaration",
    "indexEntityReference"
)
class NativeIndexerCallbacks(callback: IndexerCallback) : Structure() {
    @JvmField var abortQuery: Callback? = null
    @JvmField var diagnostic: Callback? = null
    @JvmField var enteredMainFile: EnteredMainFileCallback = EnteredMainFileCallback { _: Pointer?, mainFile: CXFile, _: Pointer? ->
        callback.enteredMainFile(mainFile.toFile())
        null
    }
    @JvmField var ppIncludedFile: Callback? = null
    @JvmField var importedASTFile: Callback? = null
    @JvmField var startedTranslationUnit: StartedTranslationUnitCallback
        = StartedTranslationUnitCallback { _: Pointer?, _: Pointer? -> callback.startedTranslationUnit() }
    @JvmField var indexDeclaration: Callback
            = IndexDeclarationCallback { _: Pointer?, info: CXIdxDeclInfo.ByReference? ->
        callback.indexDeclaration(
            DeclarationInfo(
                info!!
            )
        )
    }
    @JvmField var indexEntityReference: Callback? = null

    fun interface EnteredMainFileCallback : Callback {
        fun apply(clientData: Pointer?, mainFile: CXFile, reserved: Pointer?): Pointer?
    }

    fun interface StartedTranslationUnitCallback : Callback {
        fun apply(clientData: Pointer?, reserved: Pointer?)
    }

    fun interface IndexDeclarationCallback : Callback {
        fun apply(clientData: Pointer?, info: CXIdxDeclInfo.ByReference)
    }
}