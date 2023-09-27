package klang.jvm.binding

import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.CXCursor.CXCursorByValue

@Suppress("unused")
@Structure.FieldOrder("kind", "templateKind", "lang", "name", "USR", "cursor", "attributes", "numAttributes")
open class CXIdxEntityInfo : Structure() {
	@JvmField
    var kind = 0
	@JvmField
    var templateKind = 0
	@JvmField
    var lang = 0
	@JvmField
    var name: String = ""
	@JvmField
    var USR: String = ""
	@JvmField
    var cursor: CXCursorByValue = CXCursorByValue()
	@JvmField
    var  /* CXIdxAttrInfo */attributes: PointerByReference = PointerByReference()
	@JvmField
    var numAttributes = 0

    class ByReference : CXIdxEntityInfo(), Structure.ByReference
}
