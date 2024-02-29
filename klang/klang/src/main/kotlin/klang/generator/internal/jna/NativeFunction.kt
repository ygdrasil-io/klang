package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeFunction
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toFunctionsSpec
import klang.mapper.toInterfaceSpec
import java.io.File

private const val fileName = "Functions"

internal fun List<NativeFunction>.generateKotlinFile(outputDirectory: File, packageName: String, libraryName: String): File {

	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	val libraryInterfaceName = "${libraryName}Library"

	FileSpec.builder(packageName, fileName)
		.addProperty(generateInterfaceLibrarySpec(packageName, libraryInterfaceName, libraryName))
		.addType(toInterfaceSpec(packageName, libraryInterfaceName))
		.also { builder -> toFunctionsSpec(packageName, "lib$libraryInterfaceName").forEach(builder::addFunction) }
		.build()
		.writeTo(outputDirectory)

	return outputDirectory.resolve("$fileName.kt")
}