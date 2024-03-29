package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeFunction
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toInterfaceSpec
import java.io.File

fun List<NativeFunction>.generateKotlinFile(outputDirectory: File, packageName: String, libraryName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	val libraryInterfaceName = "${libraryName}Library"

	FileSpec.builder(packageName, "Functions")
		.addProperty(generateInterfaceLibrarySpec(packageName, libraryInterfaceName, libraryName))
		.addType(toInterfaceSpec(packageName, libraryInterfaceName))
		.build()
		.writeTo(outputDirectory)
}