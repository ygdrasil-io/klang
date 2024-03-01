package klang.generator.internal.jna

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeConstant
import klang.generator.internal.jna.helper.getSourceFile
import klang.mapper.toSpec
import java.io.File

private const val fileName = "Constants"


internal fun List<NativeConstant<*>>.generateKotlinFile(outputDirectory: File, packageName: String): File {
    check(outputDirectory.isDirectory) { "Output directory must be a directory" }
    getSpecBuilder(packageName)
        .writeTo(outputDirectory)

    return outputDirectory.getSourceFile(fileName, packageName)
}

private fun List<NativeConstant<*>>.getSpecBuilder(packageName: String): FileSpec {
    return FileSpec.builder(packageName, fileName)
        .also { builder -> forEach { builder.addProperty(it.toSpec(packageName)) } }
        .build()
}


