package klang.generator

import com.squareup.kotlinpoet.FileSpec
import klang.domain.NativeEnumeration
import klang.mapper.toSpec
import java.io.File

internal fun NativeEnumeration.generateKotlinFile(outputDirectory: String) {
	FileSpec.builder("darwin", "${name}NativeEnumeration")
		.addType(this.toSpec())
		.build()
		.writeTo(File(outputDirectory))

}