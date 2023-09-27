
package klang.jvm

import klang.jvm.binding.CXSaveError.CXSaveError_InvalidTU
import klang.jvm.binding.CXSaveError.CXSaveError_None
import klang.jvm.binding.CXSaveError.CXSaveError_TranslationErrors
import klang.jvm.binding.CXSaveError.CXSaveError_Unknown


enum class SaveError(val code: UInt) {
    None(CXSaveError_None),
    Unknown(CXSaveError_Unknown),
    TranslationErrors(CXSaveError_TranslationErrors),
    InvalidTU(CXSaveError_InvalidTU);

    companion object {
        fun of(code: UInt): SaveError {
            return SaveError.values().firstOrNull { it.code == code }
                ?: error("Invalid SaveError code: $code")
        }
    }
}