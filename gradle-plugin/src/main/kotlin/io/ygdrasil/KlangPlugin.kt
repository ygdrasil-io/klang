package io.ygdrasil

import klang.parser.json.parseAstJson
import klang.tools.dockerIsRunning
import klang.tools.generateAstFromDocker
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


open class KlangPluginExtension {
	var message: String = "Hello from GreetingPlugin"
	internal val filesToParse = mutableListOf<File>()

	fun parse(fileToParse: File) {
		filesToParse.add(fileToParse)
	}
}

class KlangPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		val extension = project.extensions.create("klang", KlangPluginExtension::class.java)

		project.task("generateBinding") {
			it.doLast {
				extension.filesToParse.forEach { fileToParse ->
					assert(fileToParse.exists()) { "File to parse does not exist" }
					assert(fileToParse.isFile()) { "${fileToParse.absolutePath} is not a file" }
					assert(fileToParse.canRead()) { "${fileToParse.absolutePath} is not readable" }
					assert(fileToParse.length() > 0) { "${fileToParse.absolutePath} is empty" }
					assert(dockerIsRunning()) { "Docker is not running" }


					val jsonFile = File.createTempFile("export", ".json")
					generateAstFromDocker(
						sourcePath = fileToParse.parentFile.absolutePath,
						sourceFile = fileToParse.name,
						clangJsonAstOutput = jsonFile
					)

					with(parseAstJson(jsonFile.absolutePath)) {
						declarations.forEach { declaration ->
							println(declaration)
						}
					}
				}
				println("Hello from the GreetingPlugin ${extension.message}")
			}
		}
	}
}

