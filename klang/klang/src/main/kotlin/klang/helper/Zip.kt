package klang.helper

import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

private object ZipHelper

private val logger = KotlinLogging.logger {}

/**
 * Unzips a file from the classpath to a target directory.
 *
 * @param sourceFile The name of the file to unzip from the classpath.
 * @param targetPath The target directory to unzip the file to.
 */
fun unzipFromClasspath(sourceFile: String, targetPath: File) {
	logger.info { "will unzip file $sourceFile to ${targetPath.absolutePath}" }
	getResourceAsStream(sourceFile)
		.decompress(targetPath)
}

/**
 * Unzips the source file to the specified target path.
 *
 * @param sourceFile The source file to unzip.
 * @param targetPath The target path to unzip the source file to.
 */
fun unzip(sourceFile: File, targetPath: File) {
	FileInputStream(sourceFile)
		.decompress(targetPath)
}

private fun getResourceAsStream(sourceFile: String) = ZipHelper::class.java.getResourceAsStream(sourceFile)
	?: error("$sourceFile not found on classpath")

private fun InputStream.decompress(targetPath: File) {
	targetPath.mkdirs()

	ZipInputStream(this).use {
		generateSequence { it.nextEntry }
			.forEach { entry ->
				val file = File(targetPath, entry.name)
				if (entry.isDirectory) {
					file.mkdirs()
				} else {
					file.parentFile.mkdirs()
					file.outputStream().use { output ->
						it.copyTo(output)
					}
				}
			}
	}
}

