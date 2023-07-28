package generation

import klang.domain.NameableDeclaration
import klang.findDeclarationByName
import klang.parser.json.parseAstJson
import java.io.File

fun main() {


	File(".")
		.absolutePath
		.let { println(it) }
	val filePath = "binding/darwin/foundation/src/main/objective-c/cocoa.m.ast.json"

	with(parseAstJson(filePath)) {

		resolve()

		findDeclarationByName<NameableDeclaration>("NSWindow")
			?.let { println(it) }
	}
}