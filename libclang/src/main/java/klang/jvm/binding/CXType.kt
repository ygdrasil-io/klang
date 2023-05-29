package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Suppress("unused")
@Structure.FieldOrder("kind", "data")
open class CXType : Structure() {
	@JvmField
    var kind = 0
	@JvmField
    var data = arrayOfNulls<Pointer>(2)

    class ByValue : CXType(), Structure.ByValue
}
