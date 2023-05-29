package klang.jvm

import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.CXIdxAttrInfo

class IndexAttribute(info: CXIdxAttrInfo) {
    enum class Kind {
        UNEXPOSED,
        IB_ACTION,
        IB_OUTLET,
        IB_OUTLET_COLLECTION
    }

    val kind: Kind
    val cursor: Cursor
    val location: IndexLocation

    init {
        kind = Kind.values()[info.kind]
        cursor = Cursor(info.cursor)
        location = IndexLocation(info.loc)
    }

    companion object {
        @JvmStatic
        fun createFromNative(attributes: PointerByReference?, numAttributes: Int): List<IndexAttribute> {
            if (attributes == null || numAttributes == 0) {
                return emptyList()
            }
            val attrInfo = CXIdxAttrInfo(attributes.value)
            val result: MutableList<IndexAttribute> = ArrayList(numAttributes)
            for (i in 0 until numAttributes) {
                // TODO: ugly hack, figure out how to make toArray() stuff work here
                val info =
                    CXIdxAttrInfo(attributes.pointer.getPointer((i * attrInfo.size() / Native.POINTER_SIZE).toLong()))
                result.add(IndexAttribute(info))
            }
            return result
        }
    }
}