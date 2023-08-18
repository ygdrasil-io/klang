package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeEnumeration
import klang.mapper.toSpec
import java.io.File

fun NativeEnumeration.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "${name}NativeEnumeration")
		.addType(this.toSpec())
		.build()
		.writeTo(outputDirectory)
}