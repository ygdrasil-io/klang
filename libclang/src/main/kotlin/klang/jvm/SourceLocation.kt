package klang.jvm

import com.sun.jna.ptr.IntByReference
import klang.jvm.binding.CXFile
import klang.jvm.binding.CXSourceLocationByValue

private typealias LocationFactory = (
    location: CXSourceLocationByValue,
    file: CXFile,
    line: IntByReference,
    column: IntByReference,
    offset: IntByReference
) -> Unit

class SourceLocation(private val ptr: CXSourceLocationByValue) {

    val fileLocation
        get() = getLocation { location: CXSourceLocationByValue, file: CXFile, line: IntByReference, column: IntByReference, offset: IntByReference ->
            Clang.getFileLocation(
                location,
                file,
                line,
                column,
                offset
            )
        }

    private fun getLocation(locationFactory: LocationFactory): Location {
        val file = CXFile()
        val line = IntByReference()
        val col = IntByReference()
        val offset = IntByReference()
        locationFactory(ptr, file, line, col, offset)

        return Location(
            getFileName(file),
            line.value.toUInt(),
            col.value.toUInt(),
            offset.value.toUInt()
        )
    }


    private fun getFileName(fileName: CXFile): String {
        return Clang.getFileName(
            fileName
        )
    }

    data class Location(
        val filename: String?,
        val line: UInt,
        val column: UInt,
        val offset: UInt
    )

}