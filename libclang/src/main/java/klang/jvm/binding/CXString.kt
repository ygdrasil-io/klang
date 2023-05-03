package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Suppress("unused")
@Structure.FieldOrder("data", "privateFlags")
class CXString : Structure() {
    @JvmField var data: Pointer? = null
    @JvmField var privateFlags = 0
}