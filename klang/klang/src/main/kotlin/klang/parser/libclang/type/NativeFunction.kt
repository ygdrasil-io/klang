package klang.parser.libclang.type

import klang.domain.NativeFunction
import klang.domain.typeOf
import klang.domain.unchecked
import klang.jvm.Cursor
import klang.jvm.DeclarationInfo
import klang.parser.libclang.ParsingContext

internal fun ParsingContext.declareFunction(info: DeclarationInfo) {
	currentDefinition = NativeFunction(
		name = info.cursor.spelling,
		returnType = typeOf(info.cursor.returnType()).unchecked("fail to create type"),
		arguments = info.cursor.arguments()
	).also(declarationRepository::save)
}

private fun Cursor.arguments() = children()
	.map { NativeFunction.Argument(it.spelling, typeOf( it.type.spelling).unchecked("fail to create type")) }

private fun Cursor.returnType() = type.spelling
	.let { it.substring(0, it.indexOf("(")) }
	.trim()

