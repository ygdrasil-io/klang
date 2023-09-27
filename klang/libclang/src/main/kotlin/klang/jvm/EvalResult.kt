package klang.jvm

import klang.jvm.binding.CXEvalResult
import klang.jvm.binding.CXEvalResultKind.CXEval_CFStr
import klang.jvm.binding.CXEvalResultKind.CXEval_Float
import klang.jvm.binding.CXEvalResultKind.CXEval_Int
import klang.jvm.binding.CXEvalResultKind.CXEval_ObjCStrLiteral
import klang.jvm.binding.CXEvalResultKind.CXEval_StrLiteral

class EvalResult(private var ptr: CXEvalResult) : AutoCloseable {


    enum class Kind {
        Integral, FloatingPoint, StrLiteral, Erroneous, Unknown
    }

    val kind: Kind
        get() {
            val code = Clang.EvalResult_getKind(ptr)
            return when (code) {
                CXEval_Int -> Kind.Integral
                CXEval_Float -> Kind.FloatingPoint
                CXEval_ObjCStrLiteral, CXEval_StrLiteral, CXEval_CFStr -> Kind.StrLiteral
                else -> Kind.Unknown
            }
        }

    val asInt: Long
        get() {
            return when (kind) {
                Kind.Integral -> Clang.EvalResult_getAsDouble(ptr)
                else -> error("Unexpected kind: $kind")
            }
        }

    val asFloat: Double
        get() {
            return when (kind) {
                Kind.FloatingPoint -> Clang.EvalResult_getAsFloat(ptr)
                else -> error("Unexpected kind: $kind")
            }
        }

    val asString: String
        get() {
            return when (kind) {
                Kind.StrLiteral -> Clang.EvalResult_getAsStr(ptr)
                else -> throw IllegalStateException("Unexpected kind: $kind")
            }
        }

    override fun close() {
        Clang.EvalResult_dispose(ptr)
    }
}