package klang.jvm

import com.sun.jna.FunctionMapper
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import klang.jvm.binding.LibClang
import java.lang.reflect.Method

fun clang_createIndex(excludeDeclarationsFromPCH: Boolean, displayDiagnostics: Boolean): Index =
    Clang.createIndex(excludeDeclarationsFromPCH, displayDiagnostics).let {
        NativePool.record(it)
    }


val Clang = Native.load(
    "clang-14.0.0", 
    LibClang::class.java,
    object : HashMap<String?, Any?>() {
    init {
        put(
            Library.OPTION_FUNCTION_MAPPER,
            FunctionMapper { _: NativeLibrary?, method: Method -> "clang_" + method.name })
    }
})
