package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Suppress("unused")
@Structure.FieldOrder("kind", "xdata", "data")
open class CXCursor : Structure() {
    @JvmField
    var kind = 0
    @JvmField
    var xdata = 0
    @JvmField
    var data = arrayOfNulls<Pointer>(3)

    class CXCursorByValue : CXCursor(), ByValue
}
