package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeFunction
import klang.mapper.generateInterfaceLibrarySpec
import klang.mapper.toInterfaceSpec
import java.io.File

fun List<NativeFunction>.generateKotlinFile(outputDirectory: File, packageName: String, libraryName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "${libraryName}FunctionLibrary")
		.addProperty(generateInterfaceLibrarySpec(libraryName, "${libraryName}FunctionLibrary"))
		.addType(toInterfaceSpec(packageName, libraryName))
		.build()
		.writeTo(outputDirectory)
}