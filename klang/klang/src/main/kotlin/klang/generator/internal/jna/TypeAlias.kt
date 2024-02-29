package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import java.io.File

private const val fileName = "TypeAlias"

internal fun List<NativeTypeAlias>.generateKotlinFile(outputDirectory: File, packageName: String): File {

	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, fileName)
		.also { fileSpec ->
			asSequence()
				.flatMap { typeAlias -> typeAlias.toSpec(packageName) }
				.forEach {
					when (it) {
						is TypeAliasSpec -> fileSpec.addTypeAlias(it)
						is TypeSpec -> fileSpec.addType(it)
					}
				}
		}
		.build()
		.writeTo(outputDirectory)

	return outputDirectory.resolve("$fileName.kt")
}