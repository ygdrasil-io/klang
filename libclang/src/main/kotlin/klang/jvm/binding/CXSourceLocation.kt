package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure

@SuppressWarnings("unused")
@Structure.FieldOrder("ptr_data", "int_data")
sealed class CXSourceLocation : Structure() {
    @JvmField
    var ptr_data = Pointer.NULL
    @JvmField
    var int_data = 0

}

class CXSourceLocationByValue : CXSourceLocation(), Structure.ByValue