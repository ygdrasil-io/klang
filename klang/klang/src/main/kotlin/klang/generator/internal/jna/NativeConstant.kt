package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeConstant
import klang.mapper.toSpec
import java.io.File

private const val fileName = "Constants"

internal fun List<NativeConstant<*>>.generateKotlinFile(outputDirectory: File, packageName: String): File {

	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "Constants")
		.also { builder -> forEach { builder.addProperty(it.toSpec(packageName)) } }
		.build()
		.writeTo(outputDirectory)

	return outputDirectory.resolve("$fileName.kt")
}