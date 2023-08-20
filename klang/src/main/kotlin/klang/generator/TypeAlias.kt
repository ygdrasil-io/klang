package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeTypeAlias
import klang.mapper.toSpec
import java.io.File

fun List<NativeTypeAlias>.generateKotlinFile(outputDirectory: File, packageName: String) {

	assert(outputDirectory.isDirectory) { "Output directory must be a directory" }

	FileSpec.builder(packageName, "TypeAlias")
		.also {
			forEach { typeAlias ->
				it.addTypeAlias(typeAlias.toSpec(packageName))
			}
		}
		.build()
		.writeTo(outputDirectory)

}