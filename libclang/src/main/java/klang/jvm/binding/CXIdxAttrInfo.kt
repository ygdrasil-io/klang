package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure
import klang.jvm.binding.CXCursor.CXCursorByValue

@Suppress("unused")
@Structure.FieldOrder("kind", "cursor", "loc")
class CXIdxAttrInfo(pointer: Pointer) : Structure(pointer) {

    var kind = 0
    var cursor = CXCursorByValue()
    var loc = CXIdxLoc.ByValue()

    init {
        read()
    }
}
