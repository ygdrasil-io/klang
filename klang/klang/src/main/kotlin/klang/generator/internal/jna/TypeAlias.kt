package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeTypeAlias
import klang.generator.internal.jna.helper.getSourceFile
import klang.mapper.toSpec
import java.io.File

private const val fileName = "TypeAlias"

internal fun List<NativeTypeAlias>.generateKotlinFile(outputDirectory: File, packageName: String): File {
	check(outputDirectory.isDirectory) { "Output directory must be a directory" }

	generateFileSpec(this, packageName)
		.build()
		.writeTo(outputDirectory)

	return outputDirectory.getSourceFile(fileName, packageName)
}

private fun generateFileSpec(typeAliases: List<NativeTypeAlias>, packageName: String): FileSpec.Builder {
	return FileSpec.builder(packageName, fileName)
		.apply {
			typeAliases
				.asSequence()
				.flatMap { typeAlias -> typeAlias.toSpec(packageName) }
				.forEach {
					when (it) {
						is TypeAliasSpec -> addTypeAlias(it)
						is TypeSpec -> addType(it)
					}
				}
		}
}