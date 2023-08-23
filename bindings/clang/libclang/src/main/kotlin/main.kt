import com.sun.jna.Pointer
import libclang.*
import java.io.File

val NULL = Pointer(0)

fun main() {
	val sourcePath = File("bindings/clang/libclang/build/klang/49883aae7961c571727e51b05be16b4d/clang-c/Index.h")

	if (sourcePath.exists().not()) {
		println("Source file does not exist")
		return
	}

	val args = listOf(
		"-I",
		sourcePath.parentFile.parentFile.absolutePath
	)

	val index = libclangLibrary.clang_createIndex(0, 0)
	val translationUnit: Pointer? = libclangLibrary.clang_parseTranslationUnit(
		index,
		sourcePath.absolutePath, args, args.size,
		NULL, 0,
		CXTranslationUnit_Flags.CXTranslationUnit_None.nativeValue.toInt()
	)

	if (translationUnit == null) {
		println("Failed to parse translation unit")
		return
	}

	val numDiagnostics = libclangLibrary.clang_getNumDiagnostics(translationUnit)
	(0 until numDiagnostics)
		.map { libclangLibrary.clang_getDiagnostic(translationUnit, it) }
		.forEach {
			println(
				libclangLibrary.clang_getCString(
					libclangLibrary.clang_formatDiagnostic(
						it,
						CXDiagnosticDisplayOptions.CXDiagnostic_DisplaySourceLocation.nativeValue.toInt() or CXDiagnosticDisplayOptions.CXDiagnostic_DisplayColumn.nativeValue.toInt()
					)
				)
			)
			libclangLibrary.clang_disposeDiagnostic(it)
		}

	val cursor = libclangLibrary.clang_getTranslationUnitCursor(translationUnit)

	val visitor = Visitor()

	libclangLibrary.clang_visitChildren(cursor, visitor, NULL)

	libclangLibrary.clang_disposeTranslationUnit(translationUnit)
	libclangLibrary.clang_disposeIndex(index)
}

class Visitor : CXCursorVisitor {
	override fun callback(cursor: CXCursor, parent: CXCursor, clientData: Pointer): Int {
		println("Cursor kind: ${libclangLibrary.clang_getCursorKind(cursor)}")

		return CXChildVisitResult.CXChildVisit_Recurse.nativeValue.toInt()
	}
}