package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Suppress("unused")
@Structure.FieldOrder("ptr_data", "int_data")
open class CXIdxLoc : Structure() {
	@JvmField
    var ptr_data = arrayOfNulls<Pointer>(2)
	@JvmField
    var int_data = 0

    class ByValue : CXIdxLoc(), Structure.ByValue
}
