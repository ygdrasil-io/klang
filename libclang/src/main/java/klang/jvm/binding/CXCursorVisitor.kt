package klang.jvm.binding

import com.sun.jna.Callback
import com.sun.jna.Pointer

fun interface CXCursorVisitor : Callback {
	fun apply(cursor: CXCursor.CXCursorByValue, parent: CXCursor.CXCursorByValue, client_data: Pointer?) : Int
}

