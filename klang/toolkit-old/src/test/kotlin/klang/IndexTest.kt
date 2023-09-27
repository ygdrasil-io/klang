package klang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.jvm.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class IndexTest : StringSpec({

    afterTest { (_, _) ->
        NativePool.disposeAll()
    }

    "test IndexSourceFileWithEmptyCallback" {
        val unit = indexTestDeclarations(IndexerCallback.DO_NOTHING)
        unit.diagnostics.isEmpty() shouldBe true
    }

    "test StartedTranslationUnit" {
        var started = false
        indexTestDeclarations(object : AbstractIndexerCallback() {
            override fun startedTranslationUnit() {
                started = true
            }
        })
        started shouldBe true
    }

    "test EnteredMainFile" {
        var handle:File? = null
        indexTestDeclarations(object : AbstractIndexerCallback() {
            override fun enteredMainFile(mainFile: File) {
                handle = mainFile
            }
        })

        handle shouldNotBe null
        handle!!.absoluteFile shouldBe File(getTestDeclarationsFile()).absoluteFile

    }

    "test IndexDeclaration" {
        val sw = StringWriter()
        val out = PrintWriter(sw)
        indexTestDeclarations(object : AbstractIndexerCallback() {
            override fun indexDeclaration(info: DeclarationInfo) {
                val cursor = info.cursor
                val location = info.location
                out.print(location.getLine().toString() + ":" + location.getColumn())
                out.print(" " + cursor.kind.spelling)
                out.print(" " + cursor.type.kind)
                out.print(" " + nonEmptyCursorSpelling(cursor))
                out.println()
                val entityInfo = info.entityInfo
                out.println("  " + entityInfo.USR)
                out.println("  " + entityInfo.kind)
                val spelling = cursor.spelling
                entityInfo.name shouldBe spelling.ifEmpty { null }
                if (info.isRedeclaration) out.println("  redecl")
                if (info.isDefinition) out.println("  def")
                if (info.isContainer) out.println("  cnt")
                if (info.isImplicit) out.println("  implicit")
            }
        })
        out.close()
        createOrCompare(sw.toString(), getDir() + "indexDeclaration.txt")
    }

    "test IndexObjCAttributes" {
        val sw = StringWriter()
        val out = PrintWriter(sw)
        val index = createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false)
        index.indexSourceFile(object : AbstractIndexerCallback() {
            override fun indexDeclaration(info: DeclarationInfo) {
                out.println(info.cursor.spelling)
                for (attribute in info.attributes) {
                    val location = attribute.location
                    out.print("  " + location.getLine() + ":" + location.getColumn())
                    out.print(" " + attribute.kind)
                    out.print(" " + attribute.cursor.kind)
                    out.println()
                }
            }
        }, getDir() + "objcAttributes.h", arrayOf("-ObjC"))
        out.close()
        createOrCompare(sw.toString(), getDir() + "objcAttributes.txt")
    }

    "test IndexContainerInfo" {
        createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = false).use { index ->
            val sw = StringWriter()
            val out = PrintWriter(sw)
            index.indexSourceFile(object : AbstractIndexerCallback() {
                override fun indexDeclaration(info: DeclarationInfo) {
                    out.println(nonEmptyCursorSpelling(info.cursor))
                    printContainerCursor(info.semanticContainer)
                    printContainerCursor(info.lexicalContainer)
                    printContainerCursor(info.declAsContainer)
                }

                private fun printContainerCursor(container: ContainerInfo?) {
                    if (container == null) return
                    val cursor = container.cursor
                    out.println("  " + cursor.kind + " " + nonEmptyCursorSpelling(cursor))
                }
            }, getDir() + "containerInfo.h", arrayOf("-c", "-x", "c++"))
            out.close()
            createOrCompare(sw.toString(), getDir() + "containerInfo.txt")
        }
    }

})

private fun nonEmptyCursorSpelling(cursor: Cursor): String {
    val spelling = cursor.spelling
    return if (spelling.isEmpty()) "<no-name>" else spelling
}

private fun getDir(): String {
    return getTestDataDir().toString() + "/index/"
}

private fun getTestDeclarationsFile(): String {
    return getDir() + "declarations.h"
}

private fun indexTestDeclarations(callback: IndexerCallback): TranslationUnit {
    createIndex(excludeDeclarationsFromPCH = false, displayDiagnostics = true).use { index ->
        return index.indexSourceFile(callback, getTestDeclarationsFile(), arrayOf())
    }
}