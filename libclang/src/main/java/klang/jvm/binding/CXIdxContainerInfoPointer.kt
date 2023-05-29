package klang.jvm.binding

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.ByReference
import klang.jvm.binding.CXCursor.CXCursorByValue

@Suppress("unused")
@Structure.FieldOrder("cursor")
open class CXIdxContainerInfo(pointer: Pointer?) : Structure(pointer) {

	@JvmField
	var cursor: CXCursorByValue = CXCursorByValue()

	constructor() : this(null)
}

@Structure.FieldOrder("cursor")
class CXIdxContainerInfoReference(pointer: Pointer?) : CXIdxContainerInfo(pointer), ByReference {

	constructor() : this(null)
}