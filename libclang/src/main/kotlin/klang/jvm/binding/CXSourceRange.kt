package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder

@SuppressWarnings("unused")
@FieldOrder("ptr_data", "begin_int_data", "end_int_data")
sealed class CXSourceRange: Structure() {

    var ptr_data: Pointer = Pointer.NULL
    var begin_int_data: Int = 0
    var end_int_data: Int = 0

}

class CXSourceRangeByVale: CXSourceRange(), Structure.ByValue