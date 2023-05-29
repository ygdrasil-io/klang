package klang.jvm

import klang.jvm.binding.CXErrorCode.CXError_ASTReadError
import klang.jvm.binding.CXErrorCode.CXError_Crashed
import klang.jvm.binding.CXErrorCode.CXError_Failure
import klang.jvm.binding.CXErrorCode.CXError_InvalidArguments
import klang.jvm.binding.CXErrorCode.CXError_Success

enum class ErrorCode(val code: UInt) {

    Success(CXError_Success),
    Failure(CXError_Failure),
    Crashed(CXError_Crashed),
    InvalidArguments(CXError_InvalidArguments),
    ASTReadError(CXError_ASTReadError);


    companion object {
        fun of(code: UInt): ErrorCode {
            return ErrorCode.values().firstOrNull { it.code == code }
                ?: error("Invalid Error Code code: $code")
        }

    }
}