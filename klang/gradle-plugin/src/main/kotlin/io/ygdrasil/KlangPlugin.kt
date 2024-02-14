package io.ygdrasil

import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.domain.NativeFunction
import klang.domain.NativeStructure
import klang.domain.NativeTypeAlias
import klang.generator.generateKotlinFile
import klang.helper.HeaderManager
import klang.helper.doesNotExists
import klang.helper.isDirectoryEmpty
import klang.helper.unzip
import klang.parser.json.parseAstJson
import klang.parser.libclang.parseFile
import klang.tools.generateAstFromDocker
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

private val logger = LoggerFactory.getLogger("klang-logger")
private val hasher by lazy {
	MessageDigest.getInstance("MD5")
}
private const val taskGroup = "klang"

enum class ParsingMethod {
	Docker,
	Libclang
}

internal sealed class KlangPluginTask {
	// TODO use a value object instead of a string
	class DownloadFile(val sourceUrl: URL, val targetFile: String) : KlangPluginTask()

	// TODO use a value object instead of a string
	class Unpack(val sourceFile: String, val targetPath: String) : KlangPluginTask()

	// TODO use a value object instead of a string
	class Parse(val sourceFile: String, val sourcePath: String, val onSuccess: DeclarationRepository.() -> Unit) :
		KlangPluginTask()

	// TODO use a value object instead of a string
	class GenerateBinding(val basePackage: String, val libraryName: String) : KlangPluginTask()
}

open class KlangPluginExtension {
	internal val tasks = mutableListOf<KlangPluginTask>()
	internal var declarations: DeclarationRepository = InMemoryDeclarationRepository()
	var parsingMethod = ParsingMethod.Docker

	@Suppress("unused")
	fun unpack(urlToUnpack: String) = urlToUnpack
		.hash
		.also { hash -> tasks.add(KlangPluginTask.Unpack(urlToUnpack, hash)) }

	@Suppress("unused")
	fun parse(fileToParse: String, at: String, onSuccess: DeclarationRepository.() -> Unit = {}) {
		tasks.add(KlangPluginTask.Parse(fileToParse, at, onSuccess))
	}

	@Suppress("unused")
	fun download(urlToDownload: URL): String = urlToDownload
		.toString()
		.hash
		.also { hash -> tasks.add(KlangPluginTask.DownloadFile(urlToDownload, hash)) }

	@Suppress("unused")
	fun generateBinding(basePackage: String, libraryName: String) {
		tasks.add(KlangPluginTask.GenerateBinding(basePackage, libraryName))
	}
}

@Suppress("unused")
class KlangPlugin : Plugin<Project> {

	private val Project.workingDirectory: File
		get() = buildDir.resolve("klang").also { it.mkdirs() }

	private val Project.cHeadersDirectory: File
		get() = workingDirectory.resolve("c-headers")

	private val Project.platformSpecificHeadersDirectory: File
		get() = workingDirectory.resolve("platform-headers")

	override fun apply(project: Project) {
		val extension = project.extensions.create("klang", KlangPluginExtension::class.java)

		val unpackCHeader = project.unpackCHeaderTask()
		val downloadFile = project.downloadTask(extension)
		val unpackFile = project.unpackTask(downloadFile, extension)
		val generateAst = project.generateAstTask(unpackFile, extension).apply {
			dependsOn(unpackCHeader)
		}
		val generateBinding = generateBindingTask(project, extension).apply {
			dependsOn(generateAst)
		}

		listOf(downloadFile, unpackFile, generateAst, generateBinding, unpackCHeader)
			.forEach { it.group = taskGroup }
	}

	private fun generateBindingTask(project: Project, extension: KlangPluginExtension): Task =
		project.task("generateBinding") { task ->
			task.doFirst {
				extension.tasks
					.asSequence()
					.filterIsInstance<KlangPluginTask.GenerateBinding>()
					.forEach { task ->
						val basePackage = task.basePackage
						val outputDirectory = project.buildDir.resolve("generated/klang/")
						outputDirectory.mkdirs()
						extension.declarations
							.generateKotlinFiles(
								outputDirectory = outputDirectory,
								basePackage = basePackage,
								libraryName = task.libraryName
							)
					}
			}
		}

	private fun Project.generateAstTask(
		unpackFile: Task,
		extension: KlangPluginExtension,
	): Task = task("generateAst") { task ->
		task.dependsOn(unpackFile)
		task.doFirst {
			extension.tasks
				.asSequence()
				.filterIsInstance<KlangPluginTask.Parse>()
				.map { Triple(it.sourceFile, workingDirectory.resolve(it.sourcePath), it.onSuccess) }
				.forEach { (fileToParse, sourcePath, onSuccess) ->
					val localFileToParse = File(fileToParse)
					assert(localFileToParse.exists()) { "File to parse does not exist" }
					assert(localFileToParse.isFile()) { "${localFileToParse.absolutePath} is not a file" }
					assert(localFileToParse.canRead()) { "${localFileToParse.absolutePath} is not readable" }
					assert(localFileToParse.length() > 0) { "${localFileToParse.absolutePath} is empty" }

					extension.declarations = when (extension.parsingMethod) {
						ParsingMethod.Docker -> {
							val jsonFile = workingDirectory.resolve("${fileToParse.hash}.json")
							generateAstFromDocker(
								sourcePath = sourcePath.absolutePath,
								sourceFile = fileToParse,
								clangJsonAstOutput = jsonFile
							)
							parseAstJson(jsonFile.absolutePath)
						}

						ParsingMethod.Libclang -> {
							parseFile(
								fileToParse,
								sourcePath.absolutePath,
								HeaderManager.listPlatformHeadersFromPath(cHeadersDirectory.toPath())
							)
						}
					}.also { it.resolveTypes() }

					with(extension.declarations) {
						onSuccess()
						resolveTypes()
					}
				}
		}
	}

	private fun Project.unpackTask(
		downloadFile: Task,
		extension: KlangPluginExtension,
	): Task = task("unpackFiles") { task ->
		task.dependsOn(downloadFile)
		task.doFirst {
			extension.tasks
				.asSequence()
				.filterIsInstance<KlangPluginTask.Unpack>()
				.map {
					workingDirectory.resolve(it.sourceFile) to workingDirectory.resolve(it.targetPath)
						.also { directory -> directory.mkdirs() }
				}
				.forEach { (sourceFile, targetFile) -> unzip(sourceFile, targetFile) }
		}
	}

	private fun Project.downloadTask(
		extension: KlangPluginExtension
	): Task = task("downloadFiles") { task ->
		task.doFirst {
			extension.tasks
				.asSequence()
				.filterIsInstance<KlangPluginTask.DownloadFile>()
				.map { it.sourceUrl to workingDirectory.resolve(it.targetFile) }
				.filter { (_, targetFile) -> !targetFile.exists() && targetFile.length() == 0L }
				.forEach { (sourceUrl, targetFile) -> downloadFile(sourceUrl, targetFile) }
		}
	}

	private fun Project.unpackCHeaderTask(): Task = task("unpackCHeader") { task ->
		task.onlyIf { this.cHeadersDirectory.doesNotExists() || this.cHeadersDirectory.isDirectoryEmpty() }
		task.doFirst {
			this.cHeadersDirectory.deleteRecursively()
			HeaderManager.putPlatformHeaderAt(cHeadersDirectory.toPath())
		}
	}

}

private fun DeclarationRepository.generateKotlinFiles(outputDirectory: File, basePackage: String, libraryName: String) {

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
}

private val String.hash
	get() = toByteArray()
		.let(hasher::digest)
		.joinToString("") { "%02x".format(it) }

/**
 * Downloads a file from the specified URL and saves it to the target location.
 *
 * @param fileUrl The URL of the file to be downloaded.
 * @param targetFile The target location where the downloaded file will be saved.
 * @return The downloaded [File] object if successful, or `null` if an error occurs.
 */
fun downloadFile(fileUrl: URL, targetFile: File): File? = try {
	targetFile.parentFile.mkdirs()
	fileUrl.openStream().use { inputStream ->
		Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
	}

	logger.info("File downloaded and saved to: ${targetFile.absolutePath}")
	targetFile
} catch (e: Exception) {
	logger.error("An error occurred: ${e.message}")
	null
}