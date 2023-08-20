package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeEnumeration
import klang.mapper.toSpec
import java.io.File

fun List<NativeEnumeration>.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "Enumerations")
		.also { builder -> forEach { builder.addType(it.toSpec()) } }
		.build()
		.writeTo(outputDirectory)
}