package klang.generator

import generation.toKotlinType
import klang.domain.ObjectiveCClass
import java.io.File

internal fun ObjectiveCClass.generateKotlinFile(outputDirectory: String) {
	val outputFile = File("$outputDirectory${name}ObjectiveCClass.kt")

	"""
package darwin

import darwin.internal.*

val ${name}Class by lazy { NSClass("${name}") }

open class ${name}(id: Long) : NSObject(id) {
${generateInstanceMethods()}

	companion object {
${generateClassMethods()}
	}
}

	""".trimIndent()
		.let(outputFile::writeText)

}

private fun ObjectiveCClass.generateClassMethods() = generateMethods(false)

private fun ObjectiveCClass.generateInstanceMethods() = generateMethods(true)

private fun ObjectiveCClass.generateMethods(instance: Boolean): String =
	methods.asSequence()
		.filter { it.instance == instance }
		.joinToString(separator = "\n") { it.generateMethod() }

private fun ObjectiveCClass.Method.generateMethod(): String {
	return "\tfun ${name.toMethodName()}(${arguments.joinToString { "${it.name}: ${it.type.toKotlinType()}" }}): ${returnType.toKotlinType()} = ObjectiveC.objc_msgSend(id, sel(\"${name}\"), ${arguments.joinToString { it.name }})"
}

private fun String.toMethodName(): String
	= split(":").first()
