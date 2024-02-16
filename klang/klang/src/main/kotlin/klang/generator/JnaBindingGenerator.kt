package klang.generator

import klang.DeclarationRepository
import klang.domain.*
import klang.generator.internal.jna.generateKotlinFile
import java.io.File

object JnaBindingGenerator: BindingGenerator {

	override fun DeclarationRepository.generateKotlinFiles(outputDirectory: File, basePackage: String, libraryName: String) {

		outputDirectory.deleteRecursively()
		outputDirectory.mkdirs()

		val declarations = findLibraryDeclaration()

		declarations
			.filterIsInstance<NativeEnumeration>()
			.generateKotlinFile(outputDirectory, basePackage)

		declarations
			.filterIsInstance<NativeFunction>()
			.generateKotlinFile(outputDirectory, basePackage, libraryName)

		declarations
			.filterIsInstance<NativeTypeAlias>()
			.generateKotlinFile(outputDirectory, basePackage)

		declarations
			.filterIsInstance<NativeStructure>()
			.generateKotlinFile(outputDirectory, basePackage)

		declarations
			.filterIsInstance<NativeConstant<*>>()
			.generateKotlinFile(outputDirectory, basePackage)
	}

}