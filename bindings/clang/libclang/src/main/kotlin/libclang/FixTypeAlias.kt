package libclang

import com.sun.jna.Callback
import com.sun.jna.Pointer
import com.sun.jna.PointerType
import com.sun.jna.Structure

typealias CXVirtualFileOverlay = Pointer
typealias CXModuleMapDescriptor = Pointer
typealias clock_t = Int
typealias time_t = Int
class CXTranslationUnit : PointerType()
typealias CXTargetInfo = Pointer
typealias CXCursorSet = Pointer
typealias CXFieldVisitor = Pointer
typealias timespec = Pointer

interface CXCursorVisitor : Callback {
	fun callback(cursor: CXCursor.ByReference, parent: CXCursor.ByReference, clientData: Pointer): Int
}

