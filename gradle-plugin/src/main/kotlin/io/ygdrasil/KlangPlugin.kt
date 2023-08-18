package io.ygdrasil

import klang.DeclarationRepository
import klang.InMemoryDeclarationRepository
import klang.domain.NativeEnumeration
import klang.domain.NativeFunction
import klang.generator.generateKotlinFile
import klang.parser.json.parseAstJson
import klang.tools.dockerIsRunning
import klang.tools.generateAstFromDocker
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.zip.ZipInputStream

private val logger = LoggerFactory.getLogger("some-logger")
private val hasher by lazy {
	MessageDigest.getInstance("MD5")
}
private const val taskGroup = "klang"

internal sealed class KlangPluginTask {
	class DownloadFile(val sourceUrl: String, val targetFile: String) : KlangPluginTask()
	class Unpack(val sourceFile: String, val targetPath: String) : KlangPluginTask()
	class Parse(val sourceFile: String, val sourcePath: String) : KlangPluginTask()
	// TODO use a value object instead of a string
	class GenerateBinding(val basePackage: String) : KlangPluginTask()
}

open class KlangPluginExtension {
	internal val tasks = mutableListOf<KlangPluginTask>()
	internal var declarations: DeclarationRepository = InMemoryDeclarationRepository()

	fun unpack(urlToUnpack: String) = urlToUnpack
		.hash
		.also { hash -> tasks.add(KlangPluginTask.Unpack(urlToUnpack, hash)) }

	fun parse(fileToParse: String, at: String) {
		tasks.add(KlangPluginTask.Parse(fileToParse, at))
	}

	fun download(urlToDownload: String): String = urlToDownload
		.hash
		.also { hash -> tasks.add(KlangPluginTask.DownloadFile(urlToDownload, hash)) }

	fun generateBinding(basePackage: String) {
		tasks.add(KlangPluginTask.GenerateBinding(basePackage))
	}
}

class KlangPlugin : Plugin<Project> {

	private val Project.workingDirectory: File
		get() = buildDir.resolve("klang").also { it.mkdirs() }

	override fun apply(project: Project) {
		val extension = project.extensions.create("klang", KlangPluginExtension::class.java)

		val downloadFile = project.downloadTask(extension)
		val unpackFile = project.unpackTask(downloadFile, extension)
		val generateAst = project.generateAstTask(unpackFile, extension)
		val generateBinding = project.task("generateBinding") { task ->
			task.dependsOn(generateAst)
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
								basePackage = basePackage
							)
					}
			}
		}

		listOf(downloadFile, unpackFile, generateAst, generateBinding)
			.forEach { it.group = taskGroup }
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
				.map { it.sourceFile to workingDirectory.resolve(it.sourcePath) }
				.forEach { (fileToParse, sourcePath) ->
					val localFileToParse = File(fileToParse)
					assert(localFileToParse.exists()) { "File to parse does not exist" }
					assert(localFileToParse.isFile()) { "${localFileToParse.absolutePath} is not a file" }
					assert(localFileToParse.canRead()) { "${localFileToParse.absolutePath} is not readable" }
					assert(localFileToParse.length() > 0) { "${localFileToParse.absolutePath} is empty" }
					assert(dockerIsRunning()) { "Docker is not running" }

					val jsonFile = workingDirectory.resolve("${fileToParse.hash}.json")
					generateAstFromDocker(
						sourcePath = sourcePath.absolutePath,
						sourceFile = fileToParse,
						clangJsonAstOutput = jsonFile
					)

					extension.declarations = parseAstJson(jsonFile.absolutePath)
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

	private fun unzip(sourceFile: File, targetPath: File) {
		ZipInputStream(FileInputStream(sourceFile)).use {
			var entry = it.nextEntry
			while (entry != null) {
				val file = File(targetPath, entry.name)
				if (entry.isDirectory) {
					file.mkdirs()
				} else {
					file.parentFile.mkdirs()
					file.outputStream().use { output ->
						it.copyTo(output)
					}
				}
				entry = it.nextEntry
			}
		}
	}
}

private fun DeclarationRepository.generateKotlinFiles(outputDirectory: File, basePackage: String) {

	outputDirectory.deleteRecursively()
	outputDirectory.mkdirs()

	declarations
		.filterIsInstance<NativeEnumeration>()
		.forEach { it.generateKotlinFile(outputDirectory, basePackage) }

	declarations.asSequence()
		.filterIsInstance<NativeFunction>()
		// Skip specific C functions
		.filter { it.name.startsWith("__").not() || it.name.startsWith("_").not() }
		.toList()
		// TODO: add mylib as plugin parameter
		.generateKotlinFile(outputDirectory, basePackage, "mylib")
}

private val String.hash
	get() = toByteArray()
		.let(hasher::digest)
		.joinToString("") { "%02x".format(it) }

private fun downloadFile(fileUrl: String, targetFile: File): File? = try {
	URL(fileUrl).openStream().use { inputStream ->
		Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
	}

	logger.info("File downloaded and saved to: ${targetFile.absolutePath}")
	targetFile
} catch (e: Exception) {
	logger.error("An error occurred: ${e.message}")
	null
}

