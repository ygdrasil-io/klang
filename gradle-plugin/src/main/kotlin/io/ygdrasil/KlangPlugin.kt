package io.ygdrasil

import klang.parser.json.parseAstJson
import klang.tools.dockerIsRunning
import klang.tools.generateAstFromDocker
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

private val logger = LoggerFactory.getLogger("some-logger")
private val hasher by lazy {
    MessageDigest.getInstance("MD5")
}

internal sealed class KlangPluginTask {
    class DownloadFile(val sourceUrl: String, val targetFile: String) : KlangPluginTask()
    class Unpack(val sourceFile: String, val targetPath: String) : KlangPluginTask()
    class Parse(val sourceFile: String, val sourcePath: String) : KlangPluginTask()
    class GenerateBinding(val sourceFile: String, val sourcePath: String) : KlangPluginTask()
}

open class KlangPluginExtension {
    internal val tasks = mutableListOf<KlangPluginTask>()

    fun unpack(urlToUnpack: String) = urlToUnpack
        .hash
        .also { hash -> tasks.add(KlangPluginTask.DownloadFile(urlToUnpack, hash)) }

    fun parse(fileToParse: String, at: String) {
        tasks.add(KlangPluginTask.Parse(fileToParse, at))
    }

    fun download(urlToDownload: String): String = urlToDownload
        .hash
        .also { hash -> tasks.add(KlangPluginTask.DownloadFile(urlToDownload, hash)) }
}

class KlangPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("klang", KlangPluginExtension::class.java)


        val downloadFile = project.task("download-files") { task ->
            extension.tasks
                .asSequence()
                .filterIsInstance<KlangPluginTask.DownloadFile>()
                .map { it.sourceUrl to task.temporaryDir.resolve(it.targetFile) }
                .filter { (_, targetFile) -> !targetFile.exists() && targetFile.length() == 0L }
                .forEach { (sourceUrl, targetFile) -> downloadFile(sourceUrl, targetFile) }
        }

        val unpackFile = project.task("unpack-files") { task ->
            task.dependsOn(downloadFile)
            extension.tasks
                .asSequence()
                .filterIsInstance<KlangPluginTask.Unpack>()
                .map {
                    it.sourceFile to task.temporaryDir.resolve(it.targetPath).also { directory -> directory.mkdirs() }
                }
                .filter { (_, targetFile) -> !targetFile.exists() }
                .forEach { (sourceFile, targetFile) -> unzip(sourceFile, targetFile) }
        }

        project.task("generateBinding") { task ->
            task.dependsOn(unpackFile)
            task.doFirst {

                extension.tasks
                    .asSequence()
                    .filterIsInstance<KlangPluginTask.Parse>()
                    .map { File(it.sourceFile) to it.sourcePath }
                    .forEach { (fileToParse, sourcePath) ->
                        assert(fileToParse.exists()) { "File to parse does not exist" }
                        assert(fileToParse.isFile()) { "${fileToParse.absolutePath} is not a file" }
                        assert(fileToParse.canRead()) { "${fileToParse.absolutePath} is not readable" }
                        assert(fileToParse.length() > 0) { "${fileToParse.absolutePath} is empty" }
                        assert(dockerIsRunning()) { "Docker is not running" }

                        val jsonFile = File.createTempFile("export", ".json")
                        generateAstFromDocker(
                            sourcePath = sourcePath,
                            sourceFile = fileToParse.name,
                            clangJsonAstOutput = jsonFile
                        )

                        with(parseAstJson(jsonFile.absolutePath)) {
                            declarations.forEach { declaration ->
                                println(declaration)
                            }
                        }
                    }
            }
        }
    }

    private fun unzip(sourceFile: String, targetPath: File) {
        TODO("Not yet implemented")
    }
}

private val String.hash
    get() = toByteArray()
        .let(hasher::digest)
        .joinToString("") { "%02x".format(it) }

private fun generateTempFile() = File.createTempFile("gradle", null)
    .also { it.deleteOnExit() }

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

