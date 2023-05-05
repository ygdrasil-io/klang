package klang.parser


import klang.jvm.*
import klang.parser.domain.declaration.Constant
import klang.parser.domain.type.Primitive
import klang.parser.domain.type.Typed
import klang.parser.domain.ParsingContext

internal class MacroParser private constructor(
    private val reparser: ClangReparser,
    private val treeMaker: TreeMaker,
    private val parsingContext: ParsingContext
) {
    val macroTable = MacroTable()

    /**
     * This method attempts to evaluate the macro. Evaluation occurs in two steps: first, an attempt is made
     * to see if the macro corresponds to a simple numeric constant. If so, the constant is parsed in Java directly.
     * If that is not possible (e.g. because the macro refers to other macro, or has a more complex grammar), fall
     * back to use clang evaluation support.
     */
    fun parseConstant(cursor: Cursor, name: String, tokens: List<String>): Constant? {
        if (cursor.isMacroFunctionLike) {
            return null
        } else if (tokens.size == 2) {
            //check for fast path
            val num = toNumber(tokens[1])
            if (num != null) {
                return cursor.createMacro(name, Primitive(Primitive.Kind.Int), num.toLong())
            }
        }
        macroTable.enterMacro(name, tokens, cursor)
        return null
    }

    private fun toNumber(str: String): Int? {
        return try {
            // Integer.decode supports '#' hex literals which is not valid in C.
            if (str.isNotEmpty() && str[0] != '#') str.toInt() else null
        } catch (nfe: NumberFormatException) {
            null
        }
    }

    /**
     * This class allows client to reparse a snippet of code against a given set of include files.
     * For performance reasons, the set of includes (which comes from the jextract parser) is compiled
     * into a precompiled header, so as to speed to incremental recompilation of the generated snippets.
     */
    internal class ClangReparser(tu: TranslationUnit, args: Collection<String>) {
        private val macro: String
        private val macroIndex = createIndex(excludeDeclarationsFromPCH = true, displayDiagnostics = false)
        private val macroUnit: TranslationUnit

        init {
            val precompiled = Files.createTempFile("jextract$", ".pch")
            tu.save(precompiled)
            macro = Files.createTempFile("jextract$", ".h")
            val patchedArgs =
                listOf( // Avoid system search path, use bundled instead
                    "-nostdinc",
                    "-ferror-limit=0",  // precompiled header
                    "-include-pch", precompiled
                ) + args
            macroUnit = macroIndex.parse(
                macro,
                { diag -> processDiagnostics(diag) },
                false,  //add serialization support (needed for macros)
                patchedArgs
            )
        }

        private fun processDiagnostics(diagnostic: Diagnostic) {
            if (PARSER_DEBUG) {
                println("Error while processing macro: " + diagnostic.spelling())
            }
        }

        fun reparse(snippet: String): List<Cursor> {
            macroUnit.reparse(
                { diag: Diagnostic -> processDiagnostics(diag) },
                listOf(Index.UnsavedFile(macro, snippet))
            )
            return macroUnit.cursor.children()
        }
    }

    /**
     * This abstraction is used to collect all macros which could not be interpreted during [.parseConstant].
     * All unparsed macros in the table can have three different states: UNPARSED (which means the macro has not been parsed yet),
     * SUCCESS (which means the macro has been parsed and has a type and a value) and FAILURE, which means the macro has been
     * parsed with some errors, but for which we were at least able to infer a type.
     *
     *
     * The reparsing process goes as follows:
     * 1. all unparsed macros are added to the table in the UNPARSED state.
     * 2. a snippet for all macros in the UNPARSED state is compiled and the table state is updated
     * 3. a recovery snippet for all macros in the FAILURE state is compiled and the table state is updated again
     * 4. we repeat from (2) until no further progress is made.
     * 5. we return a list of macro which are in the SUCCESS state.
     *
     *
     * State transitions in the table are as follows:
     * - an UNPARSED macro can go to either SUCCESS, to FAILURE or be removed (if not even a type can be inferred)
     * - a FAILURE macro can go to either SUCCESS (if recovery step succeds) or be removed
     * - a SUCCESS macro cannot go in any other state
     */
    @OptIn(ExperimentalStdlibApi::class)
    internal inner class MacroTable {
        val macrosByMangledName: MutableMap<String, Entry> = LinkedHashMap()

        internal abstract inner class Entry(val name: String, val tokens: List<String>, val cursor: Cursor) {
            fun mangledName(): String {
                return "jextract\$macro$$name"
            }

            open fun success(type: Typed, value: Any): Entry {
                throw IllegalStateException()
            }

            open fun failure(type: Typed?): Entry {
                throw IllegalStateException()
            }

            open val isSuccess = false
            open val isRecoverableFailure = false
            open val isUnparsed = false

            open fun update() {
                macrosByMangledName[mangledName()] = this
            }
        }

        internal inner class Unparsed(name: String, tokens: List<String>, cursor: Cursor) :
            Entry(name, tokens, cursor) {
            override fun success(type: Typed, value: Any): Entry {
                return Success(name, tokens, cursor, type, value)
            }

            override fun failure(type: Typed?): Entry {
                return if (type != null) RecoverableFailure(name, tokens, cursor, type) else UnparseableMacro(
                    name, tokens, cursor
                )
            }

            override val isUnparsed = true

            override fun update() {
                throw IllegalStateException()
            }
        }

        internal inner class RecoverableFailure(name: String, tokens: List<String>, cursor: Cursor, val type: Typed) :
            Entry(name, tokens, cursor) {

            override fun success(type: Typed, value: Any): Entry {
                return Success(name, tokens, cursor, this.type, value)
            }

            override fun failure(type: Typed?): Entry {
                return UnparseableMacro(name, tokens, cursor)
            }

            override val isRecoverableFailure = true
        }

        internal inner class Success(
            name: String,
            tokens: List<String>,
            cursor: Cursor,
            val type: Typed,
            val value: Any
        ) : Entry(name, tokens, cursor) {
            override val isSuccess: Boolean
                get() = true

            fun value(): Any {
                return value
            }
        }

        internal inner class UnparseableMacro(name: String, tokens: List<String>, cursor: Cursor) :
            Entry(name, tokens, cursor) {
            override fun update() {
                macrosByMangledName.remove(mangledName())
            }
        }

        fun enterMacro(name: String, tokens: List<String>, cursor: Cursor) {
            val unparsed = Unparsed(name, tokens, cursor)
            macrosByMangledName[unparsed.mangledName()] = unparsed
        }

        fun reparseConstants(): List<Constant> {
            var last = -1
            while (macrosByMangledName.size > 0 && last != macrosByMangledName.size) {
                last = macrosByMangledName.size
                // step 1 - try parsing macros as var declarations
                reparseMacros(false)
                // step 2 - retry failed parsed macros as pointers
                reparseMacros(true)
            }
            treeMaker.typeMaker.resolveTypeReferences()
            return macrosByMangledName.values
                .filter { obj: Entry -> obj.isSuccess }
                .map { e: Entry ->
                    e.cursor.createMacro(
                        e.name,
                        (e as Success).type,
                        e.value
                    )
                }
        }

        fun updateTable(typeMaker: TypeMaker, declaration: Cursor) = with(declaration) {
            val mangledName = declaration.spelling
            val entry = macrosByMangledName[mangledName]
            declaration.evaluate()?.use { result ->
                val newEntry = when (result.kind) {
                    EvalResult.Kind.Integral -> {
                        val value = result.asInt
                        entry?.success(typeMaker.makeType(declaration.type), value)
                    }
                    EvalResult.Kind.FloatingPoint -> {
                        val value = result.asFloat
                        entry?.success(typeMaker.makeType(declaration.type), value)
                    }
                    EvalResult.Kind.StrLiteral -> {
                        val value = result.asString
                        entry?.success(typeMaker.makeType(declaration.type), value)
                    }
                    else -> {
                        val type =
                            if (declaration.type == declaration.type.canonicalType) null else typeMaker.makeType(declaration.type)
                        entry?.failure(type)
                    }
                }
                newEntry?.update()
            }
        }

        fun reparseMacros(recovery: Boolean) {
            val snippet = macroDecl(recovery)
            val treeMaker = TreeMaker(parsingContext)
            try {
                reparser.reparse(snippet)
                    .filter { c: Cursor ->
                        c.kind == CursorKind.VAR_DECL &&
                                c.spelling.contains("jextract$")
                    }
                    .toList()
                    .forEach { c: Cursor -> updateTable(treeMaker.typeMaker, c) }
            } finally {
                treeMaker.typeMaker.resolveTypeReferences()
            }
        }

        fun macroDecl(recovery: Boolean): String {
            val buf = StringBuilder()
            if (recovery) {
                buf.append("#include <stdint.h>\n")
            }
            macrosByMangledName.values.asSequence()
                .filter { e: Entry -> !e.isSuccess } // skip macros that already have passed
                .filter(if (recovery)  { obj: Entry -> obj.isRecoverableFailure } else  { obj: Entry -> obj.isUnparsed })
                .forEach { e: Entry ->
                    buf.append("__auto_type ")
                        .append(e.mangledName())
                        .append(" = ")
                    if (recovery) {
                        buf.append("(uintptr_t)")
                    }
                    buf.append(e.name)
                        .append(";\n")
                }
            return buf.toString()
        }
    }

    companion object {

        fun make(treeMaker: TreeMaker, tu: TranslationUnit, args: Collection<String>, parsingContext: ParsingContext): MacroParser {
            return MacroParser(
                ClangReparser(tu, args),
                treeMaker,
                parsingContext
            )
        }
    }
}