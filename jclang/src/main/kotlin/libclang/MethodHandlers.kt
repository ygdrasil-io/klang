package libclang

import java.lang.foreign.*


val C_BOOL_LAYOUT = ValueLayout.JAVA_BOOLEAN
val C_CHAR_LAYOUT = ValueLayout.JAVA_BYTE
val C_SHORT_LAYOUT = ValueLayout.JAVA_SHORT
val C_INT_LAYOUT = ValueLayout.JAVA_INT
val C_LONG_LAYOUT = ValueLayout.JAVA_LONG
val C_LONG_LONG_LAYOUT = ValueLayout.JAVA_LONG
val C_FLOAT_LAYOUT = ValueLayout.JAVA_FLOAT
val C_DOUBLE_LAYOUT = ValueLayout.JAVA_DOUBLE
val C_POINTER_LAYOUT = ValueLayout.ADDRESS.withBitAlignment(64).asUnbounded()

val clang_disposeDiagnostic_FUNC = FunctionDescriptor.of(
	C_INT_LAYOUT,

	//CXIndexAction,
	C_POINTER_LAYOUT,
	//CXClientData client_data,
	C_POINTER_LAYOUT,
	//IndexerCallbacks *index_callbacks,
	C_POINTER_LAYOUT,
	//unsigned index_callbacks_size,
	C_INT_LAYOUT,
	//unsigned index_options,
	C_INT_LAYOUT,
	//const char *source_filename,
	C_POINTER_LAYOUT,
	//const char *const *command_line_args,
	C_POINTER_LAYOUT,
	//int num_command_line_args,
	C_INT_LAYOUT,
	//struct CXUnsavedFile *unsaved_files,
	C_POINTER_LAYOUT,
	//unsigned num_unsaved_files,
	C_INT_LAYOUT,
	//CXTranslationUnit *out_TU,
	C_POINTER_LAYOUT,
	// unsigned TU_options
	C_INT_LAYOUT

)

val clang_createIndex_method_handler = RuntimeHelper.downcallHandle(
	"clang_createIndex",
	FunctionDescriptor.of(
		C_POINTER_LAYOUT,
		C_INT_LAYOUT,
		C_INT_LAYOUT
	)
)

val clang_IndexAction_create_method_handler = RuntimeHelper.downcallHandle(
	"clang_IndexAction_create",
	FunctionDescriptor.of(C_POINTER_LAYOUT, C_POINTER_LAYOUT)
)

val clang_IndexAction_dispose_method_handler = RuntimeHelper.downcallHandle(
	"clang_IndexAction_dispose",
	FunctionDescriptor.ofVoid(C_POINTER_LAYOUT)
)


val clang_indexSourceFile_method_handler = RuntimeHelper.downcallHandle(
	"clang_indexSourceFile",
	clang_disposeDiagnostic_FUNC
)


var IndexerCallbacksLayout: GroupLayout = MemoryLayout.structLayout(
	//int (*abortQuery)(CXClientData client_data, void *reserved);
	C_POINTER_LAYOUT.withName("abortQuery"),
	//void (*diagnostic)(CXClientData client_data, CXDiagnosticSet, void *reserved);
	C_POINTER_LAYOUT.withName("diagnostic"),
	//CXIdxClientFile (*enteredMainFile)(CXClientData client_data, CXFile mainFile, void *reserved);
	C_POINTER_LAYOUT.withName("enteredMainFile"),
	//CXIdxClientFile (*ppIncludedFile)(CXClientData client_data, const CXIdxIncludedFileInfo *);
	C_POINTER_LAYOUT.withName("ppIncludedFile"),
	//CXIdxClientASTFile (*importedASTFile)(CXClientData client_data, const CXIdxImportedASTFileInfo *);
	C_POINTER_LAYOUT.withName("importedASTFile"),
	//CXIdxClientContainer (*startedTranslationUnit)(CXClientData client_data, void *reserved);
	C_POINTER_LAYOUT.withName("startedTranslationUnit"),
	//void (*indexDeclaration)(CXClientData client_data, const CXIdxDeclInfo *);
	C_POINTER_LAYOUT.withName("indexDeclaration"),
	//void (*indexEntityReference)(CXClientData client_data, const CXIdxEntityRefInfo *);
	C_POINTER_LAYOUT.withName("indexEntityReference")
)

fun interface IndexerCallbacksEnteredMainFile {
	fun apply(client_data: MemorySegment, mainFile: MemorySegment, reserved: MemorySegment): MemorySegment
}


fun main() = Arena.openConfined().use { session ->
	val index = clang_createIndex_method_handler!!.invokeExact(0, 0) as MemorySegment
	val action = clang_IndexAction_create_method_handler!!.invokeExact(index) as MemorySegment

	val client_data = session.allocate(C_POINTER_LAYOUT)
	//IndexerCallbacks *index_callbacks
	val index_callbacks = session.allocate(IndexerCallbacksLayout)
	val enteredMainFile = IndexerCallbacksEnteredMainFile { client_data, mainFile, reserved ->
		println("enteredMainFile")
		MemorySegment.NULL
	}

	val callback = RuntimeHelper.upcallStub(
		IndexerCallbacksEnteredMainFile::class.java,
		enteredMainFile,
		FunctionDescriptor.of(C_POINTER_LAYOUT, C_POINTER_LAYOUT, C_POINTER_LAYOUT, C_POINTER_LAYOUT),
		SegmentScope.global()
	)

	IndexerCallbacksLayout.varHandle(MemoryLayout.PathElement.groupElement("enteredMainFile"))
		.set(index_callbacks,callback)
	val index_callbacks_size = 0
	val index_options = 0
	val source_filename = session.allocateUtf8String("sample/c/functions.h")
	val command_line_args = MemorySegment.NULL
	val num_command_line_args = 0
	//struct CXUnsavedFile *unsaved_files,
	val CXUnsavedFile = MemorySegment.NULL
	//unsigned num_unsaved_files,
	val num_unsaved_files = 0
	//CXTranslationUnit *out_TU,
	val out_TU = session.allocate(C_POINTER_LAYOUT)
	// unsigned TU_options
	val TU_options = 0

	val error = clang_indexSourceFile_method_handler!!.invokeExact(
		action,
		client_data,
		index_callbacks,
		index_callbacks_size,
		index_options,
		source_filename,
		command_line_args,
		num_command_line_args,
		CXUnsavedFile,
		num_unsaved_files,
		out_TU,
		TU_options
	) as Int

	println("error: $error")

	clang_IndexAction_dispose_method_handler!!.invokeExact(action)

	Unit
}

/*fun clang_indexSourceFile(action: CXIndexAction, clientData: Void?, indexCallbacks: NativeIndexerCallbacks,
                    indexCallbacksSize: Int, indexOptions: Int, sourceFilename: String?, commandLineArgs: Array<String?>?,
                    numCommandLineArgs: Int, unsavedFiles: Void?, numUnsavedFiles: Int,
                    /* CXTranslationUnit */translationUnit: PointerByReference?, tuOptions: Int): Int*/

