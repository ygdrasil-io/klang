import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

plugins {
	kotlin("jvm") version libs.versions.kotlin
}



val directory = project.file("src/main/resources")
val baseUrl = "https://github.com/klang-toolkit/SDL-binary/releases/download/2.30.0/"
val fileToDownload = listOf(
	"libSDL2-aarch64.dylib" to directory.resolve("darwin-aarch64").resolve("libSDL2.dylib"),
	"libSDL2-amd64.dylib" to directory.resolve("darwin-amd64").resolve("libSDL2.dylib"),
	"libSDL2.dll" to directory.resolve("win32").resolve("libSDL2.dll"),
).forEach { (fileName, target) ->
	val url = "$baseUrl$fileName"
	val taskName = "downloadFile-$fileName"
	tasks.register<Download>(taskName) {
		onlyIf { !target.exists() }
		src(url)
		dest(target)
	}

	tasks.named("processResources") {
		dependsOn(taskName)
	}
}