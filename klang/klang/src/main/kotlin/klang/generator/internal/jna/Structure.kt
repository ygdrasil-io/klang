package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeStructure
import klang.generator.internal.jna.helper.getSourceFile
import klang.mapper.toSpec
import java.io.File

private const val fileName  = "Structures"

fun List<NativeStructure>.generateKotlinFile(outputDirectory: File, packageName: String): File {

	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, fileName)
		.also { builder -> forEach { builder.addTypes(it.toSpec(packageName)) } }
		.build()
		.writeTo(outputDirectory)

	return outputDirectory.getSourceFile(fileName, packageName)
}

private fun FileSpec.Builder.addTypes(typeSpecs: List<TypeSpec>) = typeSpecs.forEach(::addType)
