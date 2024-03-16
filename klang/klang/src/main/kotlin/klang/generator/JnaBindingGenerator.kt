package klang.generator

import klang.DeclarationRepository
import klang.domain.*
import klang.generator.internal.jna.generateKotlinFile
import java.io.File

object JnaBindingGenerator: BindingGenerator {

	override fun DeclarationRepository.generateKotlinFiles(outputDirectory: File, basePackage: String, libraryName: String): List<File> {

		outputDirectory.deleteRecursively()
		outputDirectory.mkdirs()

		val declarations = findLibraryDeclaration()
		val files = mutableListOf<File>()

		files += declarations
			.filterIsInstance<NativeEnumeration>()
			.generateKotlinFile(outputDirectory, basePackage)

		files += declarations
			.filterIsInstance<NativeFunction>()
			.generateKotlinFile(outputDirectory, basePackage, libraryName)

		files += declarations
			.filterIsInstance<NativeTypeAlias>()
			.generateKotlinFile(outputDirectory, basePackage)

		files += declarations
			.filterIsInstance<NativeStructure>()
			.generateKotlinFile(outputDirectory, basePackage)

		files += declarations
			.filterIsInstance<NativeConstant<*>>()
			.generateKotlinFile(outputDirectory, basePackage)

		return files.toList()
	}

}