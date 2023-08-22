package klang.tools

import java.io.File

class DockerCommandBuilder(
	private val dockerImage: String,
	private val directoryBindings: List<Pair<String, String>> = emptyList()
) {

	fun build() = listOf(
		"run",
		"--rm",
	) + directoryBindings.flatMap { (srcPath, targetPath) ->
		listOf("--mount", "src=\"$srcPath\",target=$targetPath,type=bind")
	} + listOf(dockerImage)

}