package klang.parser

import klang.parser.domain.MemoryLayout
import  klang.parser.domain.declaration.*
import  klang.parser.domain.declaration.Function
import  klang.parser.domain.type.*
import  klang.parser.domain.type.Array

class PrettyPrinter {

    private val SPACES = " ".repeat(92)
    var align = 0

    private fun incr() {
        align += 4
    }

    private fun decr() {
        align -= 4
    }

    private fun indent() {
        builder.append(SPACES.substring(0, align))
    }

    private var builder = StringBuilder()

    private fun getAttributes(decl: Declaration) {
        val attrs = decl.attributes.keys
        if (attrs.isEmpty()) {
            return
        }
        incr()
        indent()
        for (k in attrs) {
            builder.append("Attr: ")
            builder.append(k)
            builder.append(" -> [")
            builder.append(
                decl.attributes[k]?.joinToString(", ")
            )
            builder.append("]\n")
        }
        decr()
    }

    fun print(decl: Declaration): String {
        decl.accept()
        return builder.toString()
    }

    fun Declaration.accept() {
        when (this) {
            is Scoped -> visitScoped(this)
            is Function -> visitFunction(this)
            is Variable -> visitVariable(this)
            is Constant -> visitConstant(this)
            is Typedef -> visitTypedef(this)
        }
    }

    private fun visitScoped(d: Scoped) {
        indent()
        builder.append("Scoped: " + d.kind + " " + d.name + (d.layout?.let { " layout = $it" } ?: ""))
        builder.append("\n")
        getAttributes(d)
        incr()
        d.declarations.forEach { m: Declaration ->
            m.accept()
        }
        decr()
    }

    private fun visitFunction(d: Function) {
        indent()
        builder.append("Function: " + d.name + " type = " + d.type.accept())
        builder.append("\n")
        getAttributes(d)
        incr()
        d.parameters.forEach { m -> m.accept() }
        decr()
    }

    private fun visitVariable(d: Variable) {
        indent()
        builder.append(
            "Variable: " + d.kind + " " + d.name + " type = " + d.typed.accept() + (d.layout?.let { ", layout = $it" }
                ?: "")
        )
        builder.append("\n")
        getAttributes(d)
    }

    private fun visitConstant(d: Constant) {
        indent()
        builder.append(
            "Constant: " + d.name + " " + d.value + " type = " + d.typed.accept()
        )
        builder.append("\n")
        getAttributes(d)
    }

    private fun visitTypedef(d: Typedef) {
        indent()
        builder.append("Typedef: ").append(d.name).append(" = ")
            .append(d.typed.accept()).append("\n")
        getAttributes(d)
    }

    private fun Typed.accept(): String {
        return when (this) {
            is Primitive -> visitPrimitive(this)
            is Delegated -> visitDelegated(this)
            is Lambda -> visitLambda(this)
            is Typed.Declared -> visitDeclared(this)
            is Array -> visitArray(this)
            else -> error("not supported")
        }
    }

    private fun visitPrimitive(t: Primitive): String {
        return t.kind.toString() + (t.kind.layout?.let { layout: MemoryLayout -> "(layout = $layout)" } ?: "")
    }

    private fun visitDelegated(t: Delegated): String {
        return when (t.kind) {
            Delegated.Kind.TYPEDEF -> "typedef " + t.name + " = " + t.type.accept()
            Delegated.Kind.POINTER -> "(" + t.type.accept() + ")*"
            else -> t.kind.toString() + " = " + t.type.accept()
        }
    }

    private fun visitLambda(t: Lambda): String {
        return t.returnTyped.accept()
    }

    private fun visitDeclared(t: Typed.Declared): String {
        return "${t.reference.kind}(${t.fullName})"
    }

    private fun visitArray(t: Array): String {
        val brackets = "${if (t is Array.Vector) "v" else ""}[${t.elementCount ?: ""}]"
        return t.elementTyped.accept() + brackets
    }


}