import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import klang.tools.ClangQueryCommandBuilder
import klang.tools.runCommand
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.time.Duration


val files = listOf(
	"typedef-enum.h" to "typedef-enum.h.ast",
	"enum.h" to "enum.h.ast",
	"struct.h" to "struct.h.ast",
	"typedef-struct.h " to "typedef-struct.h.ast",
	"functions.h " to "functions.h.ast",
	"typedef.h " to "typedef.h.ast"
)

const val baseContainerDirectory = "/test_container"
const val dockerImage = "ubuntu-clang-16"
private val logger = KotlinLogging.logger {}

fun main() {
	val sourcePath = File(".")
		.resolve("clang")
		.resolve("14.0.0")
	val outputFile = sourcePath
		.resolve("temp.log")

	val dockerCommand = listOf(
		"run", "--rm", "--mount", "src=\"${sourcePath.absolutePath}\",target=$baseContainerDirectory,type=bind", dockerImage
	)

	val clangQueryCommand = ClangQueryCommandBuilder(
		buildPath = File(baseContainerDirectory),
		sourcePath = File("$baseContainerDirectory/clang-c/Index.h"),
	).build()

	"docker".runCommand(
		arguments = dockerCommand + clangQueryCommand,
		outputFile
	).apply {
		Files.readAllLines(errorOutputFile.toPath())
			.forEach { logger.error { it } }
	}
}

