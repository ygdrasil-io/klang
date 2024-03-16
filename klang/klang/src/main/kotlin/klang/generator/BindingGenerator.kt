package klang.generator

import klang.DeclarationRepository
import java.io.File

interface BindingGenerator {

	/**
	 * Generates Kotlin files based on the given declaration repository.
	 *
	 * @param outputDirectory The directory where the Kotlin files will be generated.
	 * @param basePackage The base package name for the generated Kotlin classes.
	 * @param libraryName The name of the library.
	 */
	fun DeclarationRepository.generateKotlinFiles(outputDirectory: File, basePackage: String, libraryName: String): List<File>

}