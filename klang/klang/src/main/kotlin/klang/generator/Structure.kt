package klang.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeStructure
import klang.mapper.toSpec
import java.io.File

fun List<NativeStructure>.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "Structures")
		.also { builder -> forEach { builder.addTypes(it.toSpec(packageName)) } }
		.build()
		.writeTo(outputDirectory)
}

private fun FileSpec.Builder.addTypes(typeSpecs: List<TypeSpec>) = typeSpecs.forEach(::addType)
