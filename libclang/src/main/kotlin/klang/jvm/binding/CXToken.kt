package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.ByReference
import com.sun.jna.Structure.FieldOrder

@FieldOrder("int_data", "ptr_data")
open class CXToken(pointer: Pointer? = null): Structure(pointer) {

    var int_data = intArrayOf(0, 0, 0, 0)
    var ptr_data = Pointer.NULL
}

class CXTokenByReference: CXToken(), ByReference