package klang.jvm

import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.CXFile
import klang.jvm.binding.CXIdxLoc
import java.io.File

class IndexLocation(private val location: CXIdxLoc.ByValue) {
    private var isComputed = false
    private var file: File? = null
    private var line = 0
    private var column = 0
    private var offset = 0
    private fun retrieveLocation() {
        if (isComputed) return
        isComputed = true
        val file = PointerByReference()
        val line = IntByReference()
        val column = IntByReference()
        val offset = IntByReference()
        Clang.indexLoc_getFileLocation(location, null, file, line, column, offset)
        val cxFile = CXFile()
        cxFile.pointer = file.value
        this.file = cxFile.toFile()
        this.line = line.value
        this.column = column.value
        this.offset = offset.value
    }

    // TODO: probably is null for unsaved files, investigate
    fun getFile(): File {
        retrieveLocation()
        return file!!
    }

    fun getLine(): Int {
        retrieveLocation()
        return line
    }

    fun getColumn(): Int {
        retrieveLocation()
        return column
    }

    fun getOffset(): Int {
        retrieveLocation()
        return offset
    }

    override fun toString(): String {
        return getFile().toString() + "[" + getOffset() + "]:" + getLine() + ":" + getColumn()
    }
}