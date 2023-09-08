package klang.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import java.io.File

fun List<NativeTypeAlias>.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "TypeAlias")
		.also { fileSpec ->
			asSequence()
				.map { typeAlias -> typeAlias.toSpec(packageName) }
				.forEach {
					when (it) {
						is TypeAliasSpec -> fileSpec.addTypeAlias(it)
						is TypeSpec -> fileSpec.addType(it)
					}
				}
		}
		.build()
		.writeTo(outputDirectory)

}