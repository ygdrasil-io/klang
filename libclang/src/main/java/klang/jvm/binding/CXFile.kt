package klang.jvm.binding

import com.sun.jna.PointerType
import klang.jvm.Clang
import java.io.File

class CXFile : PointerType() {
    fun toFile(): File {
        return File(Clang.getFileName(this))
    }
}
