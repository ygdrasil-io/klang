package klang.generator.internal.jna.helper

import java.io.File

internal fun File.getSourceFile(fileName: String, packageName: String): File {
	var outputDirectory = this
	packageName.split(".").forEach {
		outputDirectory = outputDirectory.resolve(it)
	}
	return outputDirectory.resolve(fileName.toKotlinFile())
}

private const val kotlinExtension = ".kt"

private fun String.toKotlinFile(): String {
	return "$this$kotlinExtension"
}
