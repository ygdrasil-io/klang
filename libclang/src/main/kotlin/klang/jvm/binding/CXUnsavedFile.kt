package klang.jvm.binding

import com.sun.jna.Structure

@SuppressWarnings("unused")
@Structure.FieldOrder("filename", "contents", "length")
class CXUnsavedFile : Structure() {
    @JvmField
    var filename: String = ""
    @JvmField
    var contents: String = ""
    @JvmField
    var length: Int = 0
}