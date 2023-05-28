package klang.parser.libclang.type

import klang.DeclarationRepository
import klang.domain.NativeFunction
import klang.jvm.Cursor
import klang.jvm.DeclarationInfo
import klang.parser.json.domain.TranslationUnitKind
import klang.parser.json.domain.TranslationUnitNode
import klang.parser.json.domain.json
import klang.parser.libclang.ParsingContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun ParsingContext.declareFunction(info: DeclarationInfo) {
	currentDefinition = NativeFunction(
		name = info.cursor.spelling,
		returnType = info.cursor.returnType(),
		arguments = info.cursor.arguments()
	).also(DeclarationRepository::save)
}

private fun Cursor.arguments() = children()
	.map { NativeFunction.Argument(it.spelling, it.type.spelling) }

private fun Cursor.returnType() = type.spelling
	.let { it.substring(0, it.indexOf("(")) }
	.trim()

