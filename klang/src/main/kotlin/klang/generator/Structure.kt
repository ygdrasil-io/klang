package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeStructure
import klang.mapper.toSpec
import java.io.File

fun List<NativeStructure>.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "Structures")
		.also { builder -> forEach { builder.addType(it.toSpec(packageName)) } }
		.build()
		.writeTo(outputDirectory)
}