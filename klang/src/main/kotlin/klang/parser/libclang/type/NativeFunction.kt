package klang.parser.libclang.type

import klang.domain.NativeFunction
import klang.jvm.Cursor
import klang.jvm.DeclarationInfo
import klang.parser.libclang.ParsingContext

internal fun ParsingContext.declareFunction(info: DeclarationInfo) {
	currentDefinition = NativeFunction(
		name = info.cursor.spelling,
		returnType = info.cursor.returnType(),
		arguments = info.cursor.arguments()
	).also(declarationRepository::save)
}

private fun Cursor.arguments() = children()
	.map { NativeFunction.Argument(it.spelling, it.type.spelling) }

private fun Cursor.returnType() = type.spelling
	.let { it.substring(0, it.indexOf("(")) }
	.trim()

