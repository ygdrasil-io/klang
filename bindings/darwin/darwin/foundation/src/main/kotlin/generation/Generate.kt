package generation

import klang.domain.NameableDeclaration
import klang.domain.NativeDeclaration
import klang.domain.NativeEnumeration
import klang.domain.ObjectiveCClass
import klang.findDeclarationByName
import klang.generator.JnaBindingGenerator
import klang.generator.generateKotlinFile
import klang.parser.json.ParserRepository
import klang.parser.json.parseAstJson
import java.io.File

const val baseDirectory = "binding/darwin/darwin/foundation/"
const val astPath = "${baseDirectory}src/main/objective-c/cocoa.m.ast.json"
const val outputDirectory = "${baseDirectory}src/main/generated/"

fun main() {

	File(".")
		.absolutePath
		.let { println(it) }

	with(parseAstJson(astPath)) {


		ParserRepository.errors
			.forEach {
				it.printStackTrace()
			}

		resolveTypes()

		findDeclarationByName<NameableDeclaration>("NSWindow")
			?.let { println(it) }

		cleanupTargetPath()

		 with(JnaBindingGenerator) {
			 generateKotlinFiles(File(outputDirectory), "darwin", "na")
		 }

		declarations
			.filterIsInstance<NameableDeclaration>()
			.filter { it.name.toString() != "NSString" }
			.filter { it.name.toString() == "NSWindow" || it is NativeEnumeration }
			.forEach(::generateKotlinFile)

	}


}

private fun generateKotlinFile(declaration: NativeDeclaration) {
	when (declaration) {
		is ObjectiveCClass -> declaration.generateKotlinFile("${outputDirectory}darwin/")
		//is NativeEnumeration -> declaration.toSpecAsEnumeration("darwin")//outputDirectory)
		else -> println("Not implemented: $declaration")
	}
}


private fun cleanupTargetPath() {
	outputDirectory
		.let(::File)
		.also(File::deleteRecursively)
		.mkdirs()
}