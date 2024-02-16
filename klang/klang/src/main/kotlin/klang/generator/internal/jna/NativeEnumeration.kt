package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeEnumeration
import klang.mapper.toSpecAsEnumeration
import java.io.File

internal fun List<NativeEnumeration>.generateKotlinFile(outputDirectory: File, packageName: String) {

	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "Enumerations")
		.also { builder -> forEach { builder.addType(it.toSpecAsEnumeration(packageName)) } }
		.build()
		.writeTo(outputDirectory)
}