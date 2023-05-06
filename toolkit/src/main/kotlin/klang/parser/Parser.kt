package klang.parser

import klang.jvm.*
import klang.parser.domain.ParsingContext
import klang.parser.domain.declaration.Declaration
import klang.parser.domain.declaration.Function
import klang.parser.domain.declaration.Scoped
import klang.parser.domain.declaration.toplevel

const val PARSER_DEBUG = true

@OptIn(ExperimentalStdlibApi::class)
class Parser {

    private val treeMaker: TreeMaker
    private val parsingContext = ParsingContext().apply {
        treeMaker = TreeMaker(this)
    }

    fun parse(path: List<String>, args: List<String>): Scoped {
        return parsingContext.parseInternal(path, args)
    }

    private fun ParsingContext.parseInternal(path: List<String>, args: List<String>): Scoped {
        createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false).use { index ->
            val translationUnit = index.parse(
                path.toString(),
                {
                    if (it.severity > Diagnostic.Severity.WARNING) {
                        println(it.toString())
                    }
                },
                true, args
            )

            //dirtyPrint(translationUnit)
            val macroParser = MacroParser.make(treeMaker, translationUnit, args, this)
            val declarations = mutableListOf<Declaration>()
            translationUnit.cursor.children()
                .forEach { children ->
                    val sourceLocation = children.sourceLocation ?: return@forEach
                    val fileLocation = sourceLocation.fileLocation
                    if (children.isDeclaration) {
                        if (children.kind == CursorKind.UNEXPOSED_DECL) {
                            children.children()
                                .mapNotNull { treeMaker.createTree(it) }
                                .forEach { e: Declaration -> declarations.add(e) }
                        } else {
                            treeMaker.createTree(children)
                                ?.let { declarations.add(it) }
                        }
                    } else if (children.isMacro && fileLocation.filename != null) {
                        children.extent?.let { range ->
                            val tokens = children.translationUnit.tokens(range)
                            val constant = macroParser.parseConstant(children, children.spelling, tokens)
                            if (constant != null) {
                                declarations.add(constant)
                            }
                        }
                    }
                }
            declarations.forEach { it.resolveNotResolveType() }
            declarations.addAll(macroParser.macroTable.reparseConstants())
            println(translationUnit.cursor.prettyPrinted())
            return toplevel(translationUnit.cursor, declarations).apply {
                treeMaker.freeze()

            }
        }
    }

    private fun Declaration.resolveNotResolveType() {
        when (this) {
            is Scoped -> {
                declarations.forEach { it.resolveNotResolveType() }
            }

            is Function -> parameters.forEach { it.resolveNotResolveType() }
        }
    }

    private fun dirtyPrint(translationUnit: TranslationUnit) {
        dirtyPrint(translationUnit.cursor)
    }

    private fun dirtyPrint(cursor: Cursor, level: Int = 0) {
        //if (cursor.sourceLocation?.isFromMainFile == false) return
        repeat(level) { print("-") }
        println("${cursor.kind.name}(${cursor.spelling}) : ${cursor.sourceLocation?.fileLocation}")
        cursor.children()
            .forEach { dirtyPrint(it, level + 1) }
    }

}