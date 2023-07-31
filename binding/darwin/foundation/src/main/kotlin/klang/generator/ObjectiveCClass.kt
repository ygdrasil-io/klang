package klang.generator

import klang.domain.ObjectiveCClass
import java.io.File

internal fun ObjectiveCClass.generateKotlinFile(outputDirectory: String) {
	val outputFile = File("$outputDirectory${name}ObjectiveCClass.kt")

	"""
package darwin
		
import darwin.internal.*
		
val ${name}Class by lazy { NSClass("${name}") }

open class ${name}(id: Long) : NSObject(id) {
${generateMethods()}

	companion object {
${generateMethods()}
	}
}

	""".trimIndent()
		.let(outputFile::writeText)

}

private fun ObjectiveCClass.generateMethods(): String =
	methods.map { it.generateMethod() }
		.joinToString(separator = "\n")

private fun ObjectiveCClass.Method.generateMethod(): String {
	return "\tfun ${name.toMethodName()}(${arguments.joinToString { "${it.name}: ${it.type}" }}): $returnType = ObjectiveC.objc_msgSend(id, sel(\"${name}\"), ${arguments.joinToString { it.name }})"
}

private fun String.toMethodName(): String
	= split(":").first()
