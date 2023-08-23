package libclang

import com.sun.jna.Callback
import com.sun.jna.Pointer

typealias CXVirtualFileOverlay = Pointer
typealias CXModuleMapDescriptor = Pointer
typealias clock_t = Int
typealias time_t = Int
typealias CXTranslationUnit = Pointer
typealias CXTargetInfo = Pointer
typealias CXCursorSet = Pointer
typealias CXFieldVisitor = Pointer
typealias timespec = Pointer

interface CXCursorVisitor : Callback {
	fun callback(cursor: CXCursor, parent: CXCursor, clientData: Pointer): Int
}

